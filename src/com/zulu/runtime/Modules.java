package com.zulu.runtime;

import com.zulu.utils.SourceLoader;
import com.zulu.utils.TimeMeasurement;
import com.zulu.utils.SerializeUtils;
import com.zulu.utils.PidValues;
import com.zulu.utils.AST;
import com.zulu.utils.BarleyException;
import com.zulu.utils.Function;
import com.zulu.utils.EntryPoint;
import com.zulu.utils.GeneratorSkip;
import com.zulu.utils.Arguments;
import com.zulu.utils.Handler;
import com.zulu.monty.Monty;
import com.zulu.reflection.Reflection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

public class Modules {

	private static final HashMap<String, HashMap<String, Function>> modules = new HashMap<>();
	public static HashMap<String, String> docs = new HashMap<>();

	private static JFrame frame;
	private static CanvasPanel panel;
	private static Graphics2D graphics;
	private static BufferedImage img;

	private static ZuluValue lastKey = new ZuluNumber(-1);
	private static ZuluList mouseHover = new ZuluList(new ZuluNumber(0), new ZuluNumber(0));

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	private static void initIo() { // TDOD: Remove this module as it implemented on the language level
		HashMap<String, Function> io = new HashMap<>();
		io.put("write", args -> {
			Arguments.check(1, args.length);
			System.out.print(args[0].toString());
			return new ZuluAtom(AtomTable.put("ok"));
		});
		io.put("writeln", args -> {
			Arguments.check(1, args.length);
			System.out.println(args[0]);
			return new ZuluAtom(AtomTable.put("ok"));
		});
		io.put("format", args -> {
			Arguments.checkAtLeast(1, args.length);

			final String format = args[0].toString();
			final Object[] values = new Object[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				if (args[i] instanceof ZuluNumber number) {
					values[i - 1] = number.asFloat().intValue();
				} else {
					values[i - 1] = args[i].toString();
				}
			}
			return new ZuluString(String.format(format, values));
		});
		io.put("fwrite", args -> {
			Arguments.checkAtLeast(1, args.length);

			final String format = args[0].toString();
			final Object[] values = new Object[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				if (args[i] instanceof ZuluNumber number) {
					values[i - 1] = number.asFloat().intValue();
				} else {
					values[i - 1] = args[i].toString();
				}
			}
			System.out.print(String.format(format, values));
			return new ZuluAtom(AtomTable.put("ok"));
		});
		io.put("fwriteln", args -> {
			Arguments.checkAtLeast(1, args.length);

			final String format = args[0].toString();
			final Object[] values = new Object[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				if (args[i] instanceof ZuluNumber number) {
					values[i - 1] = number.asFloat().doubleValue();
				} else {
					values[i - 1] = args[i].toString();
				}
			}
			System.out.printf((format) + "%n", values);
			return new ZuluAtom(AtomTable.put("ok"));
		});
		io.put("readline", args -> {
			Arguments.check(0, args.length);
			return new ZuluString(new Scanner(System.in).nextLine());
		});

		modules.put("io", io);
	}

	private static void initBts() {
		HashMap<String, Function> bts = new HashMap<>();
		bts.put("new", args -> {
			Arguments.check(0, args.length);
			return new ZuluReference(new HashMap<ZuluValue, ZuluValue>());
		});
		bts.put("insert", args -> {
			Arguments.check(3, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			((HashMap<ZuluValue, ZuluValue>) ref.getRef()).put(args[1], args[2]);
			return ref;
		});
		bts.put("tabtolist", args -> {
			Arguments.check(1, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			LinkedList<ZuluValue> result = new LinkedList<>();
			for (Map.Entry<ZuluValue, ZuluValue> entry : map.entrySet()) {
				LinkedList<ZuluValue> temporal = new LinkedList<>();
				temporal.add(entry.getKey());
				temporal.add(entry.getValue());
				result.add(new ZuluList(temporal));
			}
			return new ZuluList(result);
		});

		bts.put("tab_to_list", bts.get("tabtolist"));

		bts.put("member", args -> {
			Arguments.check(2, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			return new ZuluAtom(AtomTable.put(String.valueOf(map.containsKey(args[1]))));
		});
		bts.put("lookup", args -> {
			Arguments.check(2, args.length);
			if (!(args[0] instanceof ZuluReference ref)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			if (!(map.containsKey(args[1]))) {
				throw new BarleyException("BadArg", "map is empty or doesn't contains key: \n" + args[1] + "");
			}
			return map.get(args[1]);
		});
		bts.put("remove", args -> {
			Arguments.check(2, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			map.remove(args[1]);
			return ref;
		});
		bts.put("merge", args -> {
			Arguments.check(2, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			if (!(args[1] instanceof ZuluReference r)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			HashMap<ZuluValue, ZuluValue> m = (HashMap<ZuluValue, ZuluValue>) r.getRef();
			HashMap<ZuluValue, ZuluValue> result = new HashMap<>(map);
			result.putAll(m);
			return new ZuluReference(result);
		});
		bts.put("copy", args -> {
			Arguments.check(1, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			ZuluReference ref = (ZuluReference) args[0];
			HashMap<ZuluValue, ZuluValue> map = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
			HashMap<ZuluValue, ZuluValue> r = new HashMap<>();
			r.putAll(map);
			return new ZuluReference(r);
		});

		put("bts", bts);
	}

	private static String fix(String toString) {
		char[] chars = toString.toCharArray();
		ArrayList<String> chrs = new ArrayList<>();
		for (char c : chars) {
			chrs.add(String.valueOf(c));
		}
		return String.join("", chrs);
	}

	private static void initBarley() {
		HashMap<String, Function> shell = new HashMap<>();

		shell.put("expr_eval", args -> {
			List<AST> exprs = Handler.parseASTExpr(args[0].toString());
			return exprs.get(exprs.size() - 1).execute();
		});
		shell.put("ansi", args -> {
			//AnsiConsole.systemInstall();
			return new ZuluAtom("ok");
		});
		shell.put("reparse", (args -> {
			Arguments.check(1, args.length);
			try {
				Handler.handle(SourceLoader.readSource(args[0].toString()), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom(AtomTable.put("ok"));
		}));
		shell.put("f", (args -> {
			Arguments.check(0, args.length);
			Table.clear();
			return new ZuluAtom(AtomTable.put("ok"));
		}));
		shell.put("b", args -> {
			Arguments.check(0, args.length);
			System.out.println(Table.variables());
			return new ZuluAtom(AtomTable.put("ok"));
		});
		shell.put("q", args -> {
			Arguments.check(0, args.length);
			System.exit(0);
			return new ZuluAtom(AtomTable.put("exit"));
		});
		shell.put("spawn", args -> {
			Arguments.checkOrOr(0, 1, args.length);
			PidValues pid = new PidValues(getRandomNumber(0, 300), getRandomNumber(0, 300), getRandomNumber(0, 300));
			ZuluPID p = new ZuluPID(pid);
			switch (args.length) {
				case 0 -> {
					ProcessTable.put(p);
					return p;
				}
				case 1 -> {
					ProcessTable.put(p, args[0]);
					return p;
				}
			}
			return new ZuluAtom(AtomTable.put("error"));
		});
		shell.put("extract_pid", args -> {
			Arguments.check(1, args.length);
			ZuluValue val = args[0];
			if (!(val instanceof ZuluPID)) {
				throw new BarleyException("BadArgument", "expected PID as process-id");
			}
			ZuluPID pid = (ZuluPID) val;
			return ProcessTable.get(pid);
		});

		shell.put("generator_skip", args -> {
			throw new GeneratorSkip();
		});
		shell.put("is_integer", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluNumber)));
		});
		shell.put("length", args -> {
			Arguments.check(1, args.length);
			ZuluValue arg = args[0];
			if (arg instanceof ZuluString) {
				return new ZuluNumber((arg).toString().length());
			} else if (arg instanceof ZuluList) {
				return new ZuluNumber(((ZuluList) arg).getList().size());
			} else {
				throw new BarleyException("BadArg", "expected object that support length function");
			}
		});
		shell.put("binary", args -> {
			LinkedList<ZuluValue> result = new LinkedList<>();
			byte[] bytes = new byte[0];
			try {
				bytes = SerializeUtils.serialize(args[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (byte aByte : bytes) {
				result.add(new ZuluNumber(aByte));
			}
			return new ZuluList(result);
		});
		shell.put("from_binary", args -> {
			Arguments.check(1, args.length);
			LinkedList<ZuluValue> arr = ((ZuluList) args[0]).getList();
			ArrayList<Byte> b = new ArrayList<>();
			for (ZuluValue bt : arr) {
				b.add((byte) bt.asInteger().intValue());
			}
			Byte[] bs = b.toArray(new Byte[]{});
			byte[] binary = toPrimitives(bs);
			try {
				return SerializeUtils.deserialize(binary);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new ZuluAtom(AtomTable.put("error"));
		});
		shell.put("docs", args -> {
			Arguments.check(1, args.length);
			String module = args[0].toString();
			System.out.println(docs.get(module));
			return new ZuluAtom(AtomTable.put("ok"));
		});
		shell.put("range", args -> {
			Arguments.check(2, args.length);
			LinkedList<ZuluValue> result = new LinkedList<>();
			ZuluValue obj = args[1];
			int iteration = args[0].asFloat().intValue();
			for (int i = 0; i < iteration; i++) {
				result.add(obj);
			}
			return new ZuluList(result);
		});

		shell.put("nth", args -> {
			Arguments.check(2, args.length);
			ZuluList list = (ZuluList) args[0];
			int nth = args[1].asInteger().intValue();
			try {
				return list.getList().get(nth);
			} catch (IndexOutOfBoundsException ex) {
				return new ZuluAtom("end_of_list");
			}
		});
		shell.put("sublist", args -> {
			ZuluList list = (ZuluList) args[0];
			int from = args[1].asInteger().intValue();
			int to = args[2].asInteger().intValue();
			List<ZuluValue> subd = list.getList().subList(from, to);
			LinkedList<ZuluValue> result = new LinkedList<>(subd);
			return new ZuluList(result);
		});
		shell.put("threads_count", args -> new ZuluNumber(Thread.activeCount() + ProcessTable.storage.size() + ProcessTable.receives.size()));
		shell.put("ast_to_binary", args -> {
			Arguments.check(1, args.length);
			try {
				List<AST> parsed = Handler.parseAST(SourceLoader.readSource(args[0].toString()));
				byte[] binary = SerializeUtils.serialize((Serializable) parsed);
				LinkedList<ZuluValue> result = new LinkedList<>();
				for (byte b : binary) {
					result.add(new ZuluNumber(b));
				}
				return new ZuluList(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom(AtomTable.put("error"));
		});

		shell.put("compile_ast", args -> {
			Arguments.check(1, args.length);
			try {
				List<AST> parsed = Handler.parseAST(SourceLoader.readSource(args[0].toString()));
				byte[] binary = SerializeUtils.serialize((Serializable) parsed);
				try (FileWriter writer = new FileWriter(args[0].toString().split("\\.")[0] + ".ast", false)) {
					for (byte b : binary) {
						writer.append(String.valueOf(b)).append(" ");
					}
				}
				return new ZuluAtom(AtomTable.put("ok"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom(AtomTable.put("error"));
		});

		shell.put("atoms", args -> {
			Arguments.check(0, args.length);
			AtomTable.dump();
			return new ZuluAtom("ok");
		});

		shell.put("processes", args -> {
			Arguments.check(0, args.length);
			System.out.println(ProcessTable.receives);
			System.out.println(ProcessTable.storage);
			return new ZuluAtom("ok");
		});

		shell.put("free_process", args -> {
			Arguments.check(1, args.length);
			ZuluPID pid = (ZuluPID) args[0];
			ProcessTable.storage.remove(pid);
			ProcessTable.receives.remove(pid);
			return new ZuluAtom("ok");
		});

		shell.put("throw", args -> {
			Arguments.check(1, args.length);
			throw new BarleyException(args[0].toString(), args[0].toString());
		});

		shell.put("catch", args -> {
			Arguments.check(2, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			ZuluFunction c = (ZuluFunction) args[1];
			try {
				return fun.execute();
			} catch (BarleyException ex) {
				return c.execute(new ZuluAtom(ex.getType().toLowerCase(Locale.ROOT)));
			}
		});

		shell.put("sleep", args -> {
			Arguments.check(1, args.length);
			long time = args[0].asInteger().longValue();
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		shell.put("define", args -> {
			Arguments.check(2, args.length);
			String var = args[0].toString();
			ZuluValue value = args[1];
			Table.set(var, value);
			Table.define(var, value);
			Table.variables().put(var, value);
			return value;
		});

		shell.put("thread", args -> {
			Arguments.check(1, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			new Thread(fun::execute).start();
			return new ZuluAtom("ok");
		});

		shell.put("loop", args -> {
			while (true) {
				((ZuluFunction) args[0]).execute();
			}
		});

		shell.put("width", args -> {
			//return new BarleyNumber(TerminalBuilder.terminal().getWidth());
			return new ZuluNumber(0);
		});

		shell.put("os", args -> {
			Arguments.check(1, args.length);
			String cmd = args[0].toString();
			Runtime run = Runtime.getRuntime();
			Process pr = null;
			try {
				pr = run.exec(cmd);
				pr.waitFor();
				BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = "";
				while ((line = buf.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		shell.put("height", args -> {
			// return new BarleyNumber(TerminalBuilder.terminal().getHeight());
			return new ZuluNumber(0);
		});

		shell.put("date", args -> new ZuluString(new Date().toString()));

		put("erlava", shell);
	}

	private static void initMath() {
		HashMap<String, Function> math = new HashMap<>();

		math.put("pi", args -> {
			Arguments.check(0, args.length);
			return new ZuluNumber(Math.PI);
		});
		math.put("e", args -> {
			Arguments.check(0, args.length);
			return new ZuluNumber(Math.E);
		});
		math.put("floor", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.floor(args[0].asFloat().doubleValue()));
		});
		math.put("cos", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.cos(args[0].asFloat().doubleValue()));
		});
		math.put("tan", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.tan(args[0].asFloat().doubleValue()));
		});
		math.put("atan", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.atan(args[0].asFloat().doubleValue()));
		});
		math.put("random", args -> {
			Arguments.check(0, args.length);
			return new ZuluNumber(Math.random());
		});
		math.put("abs", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.abs(args[0].asFloat().doubleValue()));
		});
		math.put("acos", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.acos(args[0].asFloat().doubleValue()));
		});
		math.put("cbrt", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.cbrt(args[0].asFloat().doubleValue()));
		});
		math.put("ceil", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.ceil(args[0].asFloat().doubleValue()));
		});
		math.put("cosh", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.cosh(args[0].asFloat().doubleValue()));
		});
		math.put("exp", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.exp(args[0].asFloat().doubleValue()));
		});
		math.put("log", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(Math.log(args[0].asFloat().doubleValue()));
		});
		math.put("max", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(Math.max(args[0].asFloat().doubleValue(), args[1].asFloat().doubleValue()));
		});
		math.put("min", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(Math.min(args[0].asFloat().doubleValue(), args[1].asFloat().doubleValue()));
		});
		math.put("pow", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(Math.pow(args[0].asFloat().doubleValue(), args[1].asFloat().doubleValue()));
		});

		math.put("range", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(getRandomNumber(args[0].asInteger().intValue(), args[1].asInteger().intValue()));
		});

		math.put("rem", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(args[0].asInteger().intValue() % args[1].asInteger().intValue());
		});

		math.put("rsh", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(args[0].asFloat().intValue() >> args[1].asFloat().intValue());
		});

		math.put("lsh", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(args[0].asFloat().intValue() << args[1].asFloat().intValue());
		});

		math.put("shake", args -> {
			Arguments.check(2, args.length);
			return new ZuluNumber(args[0].asFloat().intValue() & args[1].asFloat().intValue());
		});

		modules.put("math", math);
	}

	private static void initString() {
		HashMap<String, Function> string = new HashMap<>();

		string.put("tab_count", args -> new ZuluNumber(new Results().getTabCount()));
		string.put("space_count", args -> new ZuluNumber(new Results().getSpaceCount()));
		string.put("length", args -> {
			Arguments.check(1, args.length);
			return new ZuluNumber(args[0].toString().length());
		});

		string.put("repeat", args -> {
			Arguments.check(2, args.length);
			String first = args[0].toString();
			ZuluValue second = args[1];
			if (!(second instanceof ZuluNumber)) {
				throw new BarleyException("Runtime", "Expected type of argument 2 to be a number, but found " + second);
			}

			int count = second.asInteger().intValue();
			return new ZuluString(first.repeat(count));
		});

		string.put("from_end_extract_until", args -> {
			Arguments.check(2, args.length);
			String path = args[0].toString();
			String match = args[1].toString();
			ArrayList<Character> ext = new ArrayList<>();

			for (int i = path.length() - 1; i >= 0 && path.charAt(i) != match.charAt(0); i--) {
				ext.add(path.charAt(i));
			}

			String e = "";
			for (int i = ext.size() - 1; i >= 0; i--) {
				e += ext.get(i);
			}

			return new ZuluString(e);
		});

		string.put("lower", args -> {
			Arguments.check(1, args.length);
			return new ZuluString(args[0].toString().toLowerCase(Locale.ROOT));
		});
		string.put("upper", args -> {
			Arguments.check(1, args.length);
			return new ZuluString(args[0].toString().toUpperCase(Locale.ROOT));
		});
		string.put("is_identifier", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(String.valueOf(Character.isLetterOrDigit(Character.toLowerCase(args[0].toString().charAt(0)))));
		});

		string.put("split", args -> {
			Arguments.checkOrOr(1, 2, args.length);
			LinkedList<ZuluValue> result = new LinkedList<>();
			String str = args[0].toString();
			switch (args.length) {
				case 1 -> {
					String[] parts = str.split(" ");
					for (String part : parts) {
						result.add(new ZuluString(part));
					}
					return new ZuluList(result);
				}
				case 2 -> {
					String[] parts_ = str.split(args[1].toString());
					for (String part : parts_) {
						result.add(new ZuluString(part));
					}
					return new ZuluList(result);
				}
				default ->
					throw new BarleyException("BadArg", "unexpected error was occurred");
			}
		});

		string.put("as_number", args -> {
			Arguments.check(1, args.length);
			try {
				return new ZuluNumber(Double.parseDouble(args[0].toString()));
			} catch (NumberFormatException ex) {
				return new ZuluAtom(AtomTable.put("error"));
			}
		});

		string.put("join", args -> {
			Arguments.check(2, args.length);
			LinkedList<ZuluValue> strings = ((ZuluList) args[0]).getList();
			ArrayList<String> strs = new ArrayList<>();
			for (ZuluValue str : strings) {
				strs.add(str.toString());
			}
			String delimiter = args[1].toString();
			return new ZuluString(String.join(delimiter, strs));
		});

		string.put("charat", args -> {
			Arguments.check(2, args.length);
			return new ZuluString(String.valueOf(args[0].toString().charAt(args[1].asInteger().intValue())));
		});

		string.put("replace", args -> {
			Arguments.check(3, args.length);
			return new ZuluString(args[0].toString().replaceAll(args[1].toString(), args[2].toString()));
		});

		string.put("from_bytes", args -> {
			Arguments.check(1, args.length);
			LinkedList<ZuluValue> bytes = ((ZuluList) args[0]).getList();
			byte[] bts = new byte[bytes.size()];
			for (int i = 0; i < bts.length; i++) {
				bts[i] = bytes.get(i).asInteger().byteValue();
			}
			return new ZuluString(bts);
		});

		put("string", string);
	}

	private static void initStack() {
		HashMap<String, Function> stack = new HashMap<>();

		stack.put("new", args -> {
			Arguments.check(0, args.length);
			return new ZuluReference(new Stack<ZuluValue>());
		});

		stack.put("push", args -> {
			Arguments.check(2, args.length);
			return ((Stack<ZuluValue>) ((ZuluReference) args[0]).getRef()).push(args[1]);
		});

		stack.put("is_empty", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as stack object");
			}
			Stack<ZuluValue> st = (Stack<ZuluValue>) ((ZuluReference) s).getRef();
			return new ZuluAtom(AtomTable.put(String.valueOf(st.isEmpty())));
		});

		stack.put("pop", args -> {
			Arguments.check(1, args.length);
			Stack<ZuluValue> s = ((Stack<ZuluValue>) ((ZuluReference) args[0]).getRef());
			return s.pop();
		});

		stack.put("peek", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as stack object");
			}
			Stack<ZuluValue> st = (Stack<ZuluValue>) ((ZuluReference) s).getRef();
			return st.peek();
		});

		stack.put("stack_to_list", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as stack object");
			}
			Stack<ZuluValue> st = (Stack<ZuluValue>) ((ZuluReference) s).getRef();
			LinkedList<ZuluValue> result = new LinkedList<>();
			for (ZuluValue value : st) {
				result.add(value);
			}
			return new ZuluList(result);
		});

		put("stack", stack);
	}

	private static void initTypes() {
		HashMap<String, Function> types = new HashMap<>();

		types.put("is_number", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluNumber)));
		});

		types.put("is_atom", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluAtom)));
		});

		types.put("is_function", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof Function)));
		});

		types.put("is_list", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluList)));
		});

		types.put("is_pid", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluPID)));
		});

		types.put("is_string", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(AtomTable.put(String.valueOf(args[0] instanceof ZuluString)));
		});

		types.put("ref_to_string", args -> {
			Arguments.check(1, args.length);
			return new ZuluString(((ZuluReference) args[0]).getRef().toString());
		});

		types.put("as_atom", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(args[0].toString());
		});

		put("types", types);
	}

	public static void initHashmap() {
		HashMap<String, Function> map = new HashMap<>();
		map.put("value_at_key", args -> {
			Arguments.check(2, args.length);
			ZuluValue hashref = args[0];
			ZuluValue key = args[1];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			if (!map_obj.containsKey(key)) {
				throw new BarleyException("Hash Key error", key + " is not a key in hashmap at " + hashref);
			}

			return map_obj.get(key);
		});

		map.put("key_at_value", args -> {
			Arguments.check(2, args.length);
			ZuluValue hashref = args[0];
			ZuluValue value = args[1];

			if (!(hashref instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			if (!map_obj.containsValue(value)) {
				throw new BarleyException("Hash Value error", value + " is not a value in hashmap at " + hashref);
			}

			for (Entry<ZuluValue, ZuluValue> entry : map_obj.entrySet()) {
				if (entry.getValue() == value || entry.getValue().equals(value)) {
					return entry.getKey();
				}
			}

			return new ZuluAtom("error");
		});

		map.put("remove", args -> {
			Arguments.check(2, args.length);
			ZuluValue hashref = args[0];
			ZuluValue key = args[1];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			map_obj.remove(key);
			return new ZuluReference(map_obj);
		});

		map.put("insert", args -> {
			Arguments.check(3, args.length);
			ZuluValue hashref = args[0];
			ZuluValue key = args[1];
			ZuluValue val = args[2];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			map_obj.put(key, val);
			return new ZuluReference(map_obj);
		});

		map.put("has_key", args -> {
			Arguments.check(2, args.length);
			ZuluValue hashref = args[0];
			ZuluValue key = args[1];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}
			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			boolean exists = map_obj.containsKey(key);
			return new ZuluAtom(exists ? "true" : "false");
		});

		map.put("has_value", args -> {
			Arguments.check(2, args.length);
			ZuluValue hashref = args[0];
			ZuluValue key = args[1];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}
			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			boolean exists = map_obj.containsValue(key);
			return new ZuluAtom(exists ? "true" : "false");
		});

		map.put("keys", args -> {
			Arguments.check(1, args.length);
			ZuluValue hashref = args[0];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}
			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			LinkedList<ZuluValue> keys = new LinkedList<>();
			for (Object k : map_obj.keySet().toArray()) {
				keys.add((ZuluValue) k);
			}
			return new ZuluList(keys);
		});

		map.put("values", args -> {
			Arguments.check(1, args.length);
			ZuluValue hashref = args[0];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}
			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			LinkedList<ZuluValue> keys = new LinkedList<>();
			for (Object k : map_obj.values().toArray()) {
				keys.add((ZuluValue) k);
			}
			return new ZuluList(keys);
		});

		map.put("new", args -> {
			Arguments.check(0, args.length);
			HashMap<ZuluValue, ZuluValue> map_obj = new HashMap<>();
			return new ZuluReference(map_obj);
		});

		map.put("is_empty", args -> {
			Arguments.check(1, args.length);
			ZuluValue hashref = args[0];

			if (!(hashref instanceof ZuluReference)) {
				System.out.println("" + hashref);
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			boolean has_items = map_obj.size() > 0;
			return new ZuluAtom(has_items ? "true" : "false");
		});

		map.put("to_string", args -> {
			Arguments.check(1, args.length);
			ZuluValue hashref = args[0];

			if (!(hashref instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as hashmap object");
			}

			HashMap<ZuluValue, ZuluValue> map_obj = (HashMap<ZuluValue, ZuluValue>) hashref.raw();
			String repr = "Map@" + map_obj.hashCode() + " {\n";
			int index = 0;
			int end = map_obj.size();

			for (Entry<ZuluValue, ZuluValue> entry : map_obj.entrySet()) {
				ZuluValue key = entry.getKey();
				ZuluValue value = entry.getValue();
				repr += key.toString() + " -> " + value.toString();
				if (index != end - 1) {
					repr += ", ";
				}
				repr += "\n";
				index++;
			}
			repr += "}";
			return new ZuluString(repr);
		});

		put("hashmap", map);
	}

	private static void initQueue() {
		HashMap<String, Function> queue = new HashMap<>();

		queue.put("new", args -> {
			Arguments.check(0, args.length);
			return new ZuluReference(new ConcurrentLinkedQueue<ZuluValue>());
		});

		queue.put("in", args -> {
			Arguments.check(2, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as queue object");
			}
			ConcurrentLinkedQueue<ZuluValue> st = (ConcurrentLinkedQueue<ZuluValue>) ((ZuluReference) s).getRef();
			st.add(args[1]);
			return args[1];
		});

		queue.put("out", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as queue object");
			}
			ConcurrentLinkedQueue<ZuluValue> st = (ConcurrentLinkedQueue<ZuluValue>) ((ZuluReference) s).getRef();
			return st.remove();
		});

		queue.put("peek", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as queue object");
			}
			ConcurrentLinkedQueue<ZuluValue> st = (ConcurrentLinkedQueue<ZuluValue>) ((ZuluReference) s).getRef();
			return st.peek();
		});

		queue.put("q_to_list", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as queue object");
			}
			ConcurrentLinkedQueue<ZuluValue> st = (ConcurrentLinkedQueue<ZuluValue>) ((ZuluReference) s).getRef();
			ZuluValue[] calls = st.toArray(new ZuluValue[]{});
			LinkedList<ZuluValue> result = new LinkedList<>();
			for (ZuluValue call : calls) {
				result.add(call);
			}
			return new ZuluList(result);
		});

		queue.put("is_empty", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as queue object");
			}
			ConcurrentLinkedQueue<ZuluValue> st = (ConcurrentLinkedQueue<ZuluValue>) ((ZuluReference) s).getRef();
			return new ZuluAtom(String.valueOf(st.isEmpty()));
		});

		put("queue", queue);
	}

	private static void initMeasurement() {
		HashMap<String, Function> m = new HashMap<>();

		m.put("new", args -> {
			Arguments.check(0, args.length);
			return new ZuluReference(new TimeMeasurement());
		});

		m.put("start", args -> {
			Arguments.check(2, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as measurement object");
			}
			TimeMeasurement st = (TimeMeasurement) ((ZuluReference) s).getRef();
			st.start(args[1].toString());
			return new ZuluAtom("ok");
		});

		m.put("stop", args -> {
			Arguments.check(2, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as measurement object");
			}
			TimeMeasurement st = (TimeMeasurement) ((ZuluReference) s).getRef();
			st.stop(args[1].toString());
			return new ZuluAtom("ok");
		});

		m.put("pause", args -> {
			Arguments.check(2, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as measurement object");
			}
			TimeMeasurement st = (TimeMeasurement) ((ZuluReference) s).getRef();
			st.pause(args[1].toString());
			return new ZuluAtom("ok");
		});

		m.put("summary", args -> {
			Arguments.check(1, args.length);
			ZuluValue s = args[0];
			if (!(s instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected reference as measurement object");
			}
			TimeMeasurement st = (TimeMeasurement) ((ZuluReference) s).getRef();
			System.out.println("======================");
			System.out.println(st.summary(TimeUnit.MILLISECONDS, true));
			return new ZuluAtom("ok");
		});

		put("measurement", m);
	}

	private static void initSignal() {
		HashMap<String, Function> s = new HashMap<>();
		s.put("create", args -> {
			Arguments.check(0, args.length);
			new File("tmp/").mkdir();
			try (FileWriter writer = new FileWriter("tmp/signals.txt", false)) {
				writer.append("");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		s.put("throw", args -> {
			Arguments.check(2, args.length);
			String type = args[0].toString();
			String text = args[1].toString();
			try (FileWriter writer = new FileWriter("tmp/signals.txt", false)) {
				writer.append(type + " " + text);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		s.put("on_signal", args -> {
			Arguments.check(1, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			try {
				String[] messageParts = SourceLoader.readSource("tmp/signals.txt").split(" ");
				if (List.of().isEmpty()) {
					return new ZuluAtom("empty");
				}
				String type = messageParts[0];
				String message = String.join(" ", List.of(messageParts).subList(1, messageParts.length));
				fun.execute(new ZuluString(type), new ZuluString(message));
				new FileWriter("tmp/signals.txt", false).append("").close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		s.put("on_named_signal", args -> {
			Arguments.check(2, args.length);
			String m = args[0].toString();
			ZuluFunction fun = (ZuluFunction) args[1];
			try {
				String[] messageParts = SourceLoader.readSource("tmp/signals.txt").split(" ");
				if (List.of(messageParts).isEmpty()) {
					return new ZuluAtom("empty");
				}
				String type = messageParts[0];
				if (m.equals(type)) ; else {
					return new ZuluAtom("unmatch");
				}
				String message = String.join(" ", List.of(messageParts).subList(1, messageParts.length));
				fun.execute(new ZuluString(type), new ZuluString(message));
				new FileWriter("tmp/signals.txt", false).append("").close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		put("signal", s);
	}

	private static void initCode() {
		HashMap<String, Function> code = new HashMap<>();

		code.put("load_bts", args -> {
			Arguments.check(1, args.length);
			if (!(args[0] instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "expected REFERENCE as bts table");
			}
			try {
				ZuluReference ref = (ZuluReference) args[0];
				HashMap<ZuluValue, ZuluValue> methods = (HashMap<ZuluValue, ZuluValue>) ref.getRef();
				HashMap<String, ZuluFunction> ms = new HashMap<>();
				for (Map.Entry<ZuluValue, ZuluValue> entry : methods.entrySet()) {
					ms.put(entry.getKey().toString(), (ZuluFunction) entry.getValue());
				}
				byte[] bytes = SerializeUtils.serialize(ms);
				LinkedList<ZuluValue> bs = new LinkedList<>();
				for (byte b : bytes) {
					bs.add(new ZuluNumber(b));
				}
				return new ZuluList(bs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		code.put("loaded", args -> {
			Arguments.check(1, args.length);
			return new ZuluAtom(String.valueOf(modules.containsKey(args[0].toString())));
		});

		code.put("modules", args -> {
			Arguments.check(0, args.length);
			LinkedList<ZuluValue> strings = new LinkedList<>();
			for (Map.Entry<String, HashMap<String, Function>> entry : modules.entrySet()) {
				strings.add(new ZuluString(entry.getKey()));
			}
			return new ZuluList(strings);
		});

		code.put("load_binary", args -> {
			Arguments.check(2, args.length);
			try {
				String module = args[0].toString();
				LinkedList<ZuluValue> b = ((ZuluList) args[1]).getList();
				byte[] bytes = new byte[b.size()];
				for (int i = 0; i < b.size(); i++) {
					bytes[i] = b.get(i).asInteger().byteValue();
				}
				HashMap<String, ZuluFunction> methods = SerializeUtils.deserialize(bytes);
				HashMap<String, Function> funs = new HashMap<>();
				for (Map.Entry<String, ZuluFunction> entry : methods.entrySet()) {
					funs.put(entry.getKey(), entry.getValue());
				}
				put(module, funs);
			} catch (IOException | ClassCastException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		code.put("module_content", args -> {
			Arguments.check(1, args.length);
			HashMap<String, Function> module = get(args[0].toString());
			if (module == null) {
				throw new BarleyException("BadArg", "module is not compiled '" + args[0] + "'");
			}
			HashMap<ZuluValue, ZuluValue> m = new HashMap<>();
			for (Map.Entry<String, Function> entry : module.entrySet()) {
				m.put(new ZuluAtom(entry.getKey()), new ZuluFunction(entry.getValue()));
			}
			return new ZuluReference(m);
		});

		code.put("append_module", args -> {
			Arguments.check(3, args.length);
			Function fun = ((ZuluFunction) args[0]).getFunction();
			String module = args[1].toString();
			String method = args[2].toString();
			get(module).put(method, fun);
			return new ZuluAtom("ok");
		});

		code.put("delete", args -> {
			Arguments.check(1, args.length);
			modules.remove(args[0].toString());
			return new ZuluAtom("ok");
		});

		put("code", code);
	}

	private static void initBarleyUnit() {
		HashMap<String, Function> unit = new HashMap<>();

		unit.put("assert_equals", args -> {
			Arguments.check(2, args.length);
			if (args[0].equals(args[1])) {
				return new ZuluAtom("ok");
			}
			throw new BUnitAssertionException("values are not equals: "
				+ "1: " + args[0] + ", 2: " + args[1]);
		});
		unit.put("assert_not_equals", args -> {
			Arguments.check(2, args.length);
			if ((!args[0].equals(args[1]))) {
				return new ZuluAtom("ok");
			}
			throw new BUnitAssertionException("values are not equals: "
				+ "1: " + args[0] + ", 2: " + args[1]);
		});
		unit.put("assert_true", args -> {
			Arguments.check(2, args.length);
			if (args[0].toString().equals("true")) {
				return new ZuluAtom("ok");
			}
			throw new BUnitAssertionException("values are not equals: "
				+ "1: " + args[0] + ", 2: " + args[1]);
		});
		unit.put("assert_false", args -> {
			Arguments.check(2, args.length);
			if (args[0].toString().equals("false")) {
				return new ZuluAtom("ok");
			}
			throw new BUnitAssertionException("values are not equals: "
				+ "1: " + args[0] + ", 2: " + args[1]);
		});
		unit.put("run", new runTests());

		put("b_unit", unit);
	}

	private static void initFile() {
		HashMap<String, Function> file = new HashMap<>();

		file.put("read", args -> {
			Arguments.check(1, args.length);
			try {
				return new ZuluString(SourceLoader.readSource(args[0].toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		file.put("write", args -> {
			Arguments.check(2, args.length);
			try (FileWriter writer = new FileWriter(args[0].toString(), false)) {
				writer.append(args[1].toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		file.put("append", args -> {
			Arguments.check(2, args.length);
			try (FileWriter writer = new FileWriter(args[0].toString(), true)) {
				writer.append(args[1].toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		file.put("write_bytes", args -> {
			File outputFile = new File(args[0].toString());
			try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
				LinkedList<ZuluValue> values = ((ZuluList) args[1]).getList();
				byte[] bytes = new byte[values.size()];
				for (int i = 0; i < values.size(); i++) {
					bytes[i] = values.get(i).asInteger().byteValue();
				}
				outputStream.write(bytes);
				return new ZuluAtom(("ok"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		put("file", file);
	}

	private static void initSocket() {
		HashMap<String, Function> socket = new HashMap<>();

		socket.put("server", args -> {
			System.out.println("Use of package 'socket' is not recommended. Wait for it's deprecation && removing and use new version!");
			Arguments.check(1, args.length);
			try {
				return new ZuluReference(new ServerSocket(args[0].asInteger().intValue()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		socket.put("socket", args -> {
			System.out.println("Use of package 'socket' is not recommended. Wait for it's deprecation && removing and use new version!");
			Arguments.check(2, args.length);
			String host_name = args[0].toString();
			int port = args[1].asInteger().intValue();
			try {
				return new ZuluReference(new Socket(host_name, port));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		socket.put("accept_server", args -> {
			System.out.println("Use of package 'socket' is not recommended. Wait for it's deprecation && removing and use new version!");
			Arguments.check(1, args.length);
			ServerSocket s = (ServerSocket) ((ZuluReference) args[0]).getRef();
			try {
				return new ZuluReference(s.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		put("socket", socket);
	}

	private static void initDist() {
		HashMap<String, Function> dist = new HashMap<>();

		dist.put("entry", args -> new ZuluReference(new EntryPoint(args[0].toString(), args[1].toString())));

		dist.put("bake", args -> {
			LinkedList<ZuluValue> result = new LinkedList<>();
			String name = args[0].toString();
			result.add(new ZuluList(new ZuluString("name"), new ZuluString(name)));
			String desc = args[1].toString();
			result.add(new ZuluList(new ZuluString("desc"), new ZuluString(desc)));
			LinkedList<ZuluValue> env = new LinkedList<>();
			env.add(new ZuluString("globals"));
			for (Map.Entry<String, ZuluValue> en : Table.variables().entrySet()) {
				env.add(new ZuluList(new ZuluString(en.getKey()), en.getValue()));
			}
			result.add(new ZuluList(env));
			EntryPoint entry = (EntryPoint) ((ZuluReference) args[2]).getRef();
			List<ZuluValue> modules = List.of(args).subList(3, args.length);
			LinkedList<ZuluValue> ms = new LinkedList<>();
			ms.add(new ZuluString("modules"));
			for (ZuluValue at : modules) {
				ZuluAtom atom = (ZuluAtom) at;
				String module = at.toString();
				HashMap<String, Function> methods = get(module);
				LinkedList<ZuluValue> method = new LinkedList<>();
				method.add(atom);
				for (Map.Entry<String, Function> ent : methods.entrySet()) {
					method.add(new ZuluList(new ZuluString(ent.getKey()), new ZuluFunction(ent.getValue())));
				}
				if (docs.containsKey(module)) {
					method.add(new ZuluList(new ZuluString("doc"), new ZuluString(docs.get(module))));
				} else {
					method.add(new ZuluList(new ZuluString("doc"), new ZuluString("no docs provided")));
				}
				ms.add(new ZuluList(method));
			}
			result.add(new ZuluList(ms));
			result.add(new ZuluList(new ZuluString("entry_point"), new ZuluReference(entry)));
			return new ZuluList(result);
		});

		dist.put("write", args -> {
			Arguments.check(2, args.length);
			try (FileWriter writer = new FileWriter(args[0].toString() + ".app", false)) {
				byte[] bytes = SerializeUtils.serialize(args[1]);
				StringBuilder result = new StringBuilder();
				for (byte b : bytes) {
					result.append(b).append(" ");
				}
				writer.write(result.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		dist.put("raw_app", args -> {
			Arguments.check(1, args.length);
			ZuluList root = ((ZuluList) args[0]);
			String name = ((ZuluList) root.getList().get(0))
				.getList().get(1).toString();
			String desc = ((ZuluList) root.getList().get(1))
				.getList().get(1).toString();
			Table.define("APP_NAME", new ZuluString(name));
			Table.define("APP_DESC", new ZuluString(desc));
			LinkedList<ZuluValue> globals = ((ZuluList) root.getList().get(2)).getList();
			for (ZuluValue global : globals) {
				if (global.toString().equals("globals")) {
					continue;
				}
				ZuluList g = (ZuluList) global;
				String n = g.getList().get(0).toString();
				ZuluValue val = g.getList().get(1);
				Table.define(n, val);
			}
			LinkedList<ZuluValue> modules = ((ZuluList) root.getList().get(3)).getList();
			for (ZuluValue module : modules) {
				if (module.toString().equals("modules")) {
					continue;
				}
				HashMap<String, Function> map = new HashMap<>();
				String m_name = ((ZuluList) module).getList().get(0).toString();
				List<ZuluValue> m = ((ZuluList) module).getList().subList(1, ((ZuluList) module).getList().size());
				for (ZuluValue method : m) {
					if (((ZuluList) method).getList().get(0).toString().equals("doc")) {
						docs.put(m_name, ((ZuluList) method).getList().get(1).toString());
						continue;
					}
					String f_name = ((ZuluList) method).getList().get(0).toString();
					Function f = ((ZuluFunction) ((ZuluList) method).getList().get(1)).getFunction();
					map.put(f_name, f);
				}
				put(m_name, map);
			}
			EntryPoint point = (EntryPoint) ((ZuluReference) (((ZuluList) root.getList().get(4)).getList().get(1))).getRef();
			Function main = get(point.getName()).get(point.getMethod());
			return main.execute();
		});

		dist.put("app", args -> {
			Arguments.check(1, args.length);
			Function fun = dist.get("raw_app");
			try {
				String[] bts = SourceLoader.readSource(args[0].toString()).split(" ");
				List<Byte> bs = new ArrayList<>();
				for (String str : bts) {
					bs.add(Byte.parseByte(str));
				}
				Byte[] bt = bs.toArray(new Byte[]{});
				byte[] bytes = toPrimitives(bt);
				ZuluValue app = SerializeUtils.deserialize(bytes);
				return fun.execute(app);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("error");
		});

		put("dist", dist);
	}

	private static String current() {
		return modules.toString();
	}

	private static void initAmethyst() {
		HashMap<String, Function> am = new HashMap<>();

		am.put("lexer", args -> {
			Arguments.checkOrOr(1, 2, args.length);
			try {
				String lexerFile = SourceLoader.readSource(args[0].toString());
				String result = "";
				String[] lines = lexerFile.split("\\n");
				Map<String, String> macros = new HashMap<>();
				int cutAfterRules = 1;
				for (String line : lines) {
					if (line.isEmpty() || line.isBlank()) {
						continue;
					}
					String[] parts = line.split(" ");
					cutAfterRules++;
					if (parts.length == 1) {
						break;
					}
					String id = parts[0];
					if (!parts[1].equals("=")) {
						System.err.println("Lexer Warning: Expected '=' after macros name, got '" + parts[1] + "'");
						break;
					}
					String rep = String.join(" ", List.of(parts).subList(2, parts.length));
					result += "global " + id + "\n = " + rep + "\n.\n";
				}
				List<String> rules = List.of(lines).subList(cutAfterRules, lines.length);
				int cutToCatches = 0;
				for (String line : rules) {
					if (line.length() == 7) {
						break;
					}
					cutToCatches++;
				}
				String[] sCatch = lexerFile.split("Catches");
				result += sCatch[sCatch.length - 1];
				result += "\n";
				// Rules transformation
				result += "\n";
				result += "global Pos = 0.\n";
				result += "global Line = 1.\n";
				result += "global EOFToken = [eof, -1, \"\"].\n\n";
				result += "peek(Parts, RelativePos) ->\n"
					+ "    FinalPosition = RelativePos + Pos,\n"
					+ "    lists:nth(Parts, FinalPosition).\n"
					+ "\n"
					+ "next(Parts) ->\n"
					+ "    barley:define(\"Pos\", Pos + 1),\n"
					+ "    peek(Parts, 0).\n";
				result += "illegal_character(S, L) -> barley:throw(\"illegal char '\" + S + \"' at line \" + Line).\n"
					+ "\n"
					+ "lex(String) -> lex(String, 1).\n"
					+ "\n"
					+ "lex(String, Line) ->\n"
					+ "    Pos = 0,\n"
					+ "    Line = 1,\n"
					+ "    process_parts(string:split(String, \"\")).\n";
				StringBuilder process = new StringBuilder("\n");
				for (String rule : rules) {
					if (rule.isEmpty() || rule.isBlank()) {
						continue;
					}
					String[] parts = rule.split(" ");
					StringBuilder buffer = new StringBuilder();
					if (parts[0].equals("once")) {
						String expr = macros.containsKey(parts[1]) ? rule.substring(5, rule.indexOf("->")).replaceAll(parts[1], macros.get(parts[1])) : String.format("%s", rule.substring(5, rule.indexOf("->")));
						String res = String.join(" ", List.of(parts).subList(List.of(parts).indexOf("->") + 1, parts.length));
						buffer.append("process_part(Parts, Symbol) when Symbol == \n ")
							.append(expr)
							.append("\n -> \n")
							.append("  next(Parts),\n  ")
							.append(res)
							.append("\n.\n");
						process.append(buffer);
						continue;
					}
					if (parts[0].equals("no_advance")) {
						String expr = macros.containsKey(parts[1]) ? rule.substring(5, rule.indexOf("->")).replaceAll(parts[1], macros.get(parts[1])) : String.format("%s", rule.substring(5, rule.indexOf("->")));
						String res = String.join(" ", List.of(parts).subList(List.of(parts).indexOf("->") + 1, parts.length));
						buffer.append("process_part(Parts, Symbol) when Symbol == \n")
							.append(expr)
							.append("\n -> \n")
							.append(res)
							.append("\n.\n");
						process.append(buffer);
						continue;
					}
					if (parts[0].equals("no_advance_expr")) {
						String expr = String.format("%s", rule.substring(16, rule.indexOf("->")));
						String res = String.join(" ", List.of(parts).subList(List.of(parts).indexOf("->") + 1, parts.length));
						buffer.append("process_part(Parts, Symbol) when \n")
							.append(expr)
							.append("\n -> \n  ")
							.append(res)
							.append("\n.\n");
						process.append(buffer);
						continue;
					}
					if (parts[0].equals("once_expr")) {
						String expr = String.format("%s", rule.substring(10, rule.indexOf("->")));
						String res = String.join(" ", List.of(parts).subList(List.of(parts).indexOf("->") + 1, parts.length));
						process.append("process_part(Parts, Symbol) when ").append(expr).append(" -> \n").append("  next(Parts), \n  ").append(res).append("\n");
						process.append(".\n");
						continue;
					}
					if (parts[0].equals("skip")) {
						List<String> ps = List.of(parts).subList(2, parts.length);
						String expr = String.join(" ", ps);

						process.append("process_part(Parts, Symbol)\n when Symbol == \n  ")
							.append(expr)
							.append("\n -> \n")
							.append("  next(Parts), \n  ")
							.append("[skip, Line, \"\"]")
							.append(".\n");
					}

					if (parts[0].equals("line_increase")) {
						List<String> ps = List.of(parts).subList(2, parts.length);
						String expr = String.join(" ", ps);

						process.append("process_part(Parts, Symbol)\n when Symbol == \n  ")
							.append(expr)
							.append("\n -> \n")
							.append("Line = Line + 1, Pos = Pos + 1, [skip, Line + 1, \"\"].\n");
					}

					if (parts[0].equals("anyway")) {
						String r = String.join(" ", List.of(parts).subList(2, parts.length));
						process.append("process_part(Parts, Symbol) ->\n    ")
							.append(r)
							.append("\n")
							.append(".");
					}
				}
				result += process + "\nprocess_part(Parts, Symbol) when Symbol == end_of_list -> EOFToken.\n";
				result += "\n";
				result += "process_parts(Parts) ->\n"
					+ "    Result = lists:reduce(def (X, Acc) -> First = peek(Parts, 0), Acc + [process_part(Parts, First)]. end, Parts, []),\n"
					+ "    WithoutEOF = lists:filter(def (X) -> (not (lists:nth(X, 0) == eof)). end, Result),\n"
					+ "    WithoutEOF = lists:filter(def (X) -> (not (lists:nth(X, 0) == skip)). end, WithoutEOF),\n"
					+ "    WithoutEOF = WithoutEOF + [EOFToken].";
				result = "-module(" + (args.length == 1 ? args[0].toString().split("\\.")[0] : args[1].toString()) + ").\n\n" + result;
				try (FileWriter writer = new FileWriter(args[0].toString().split("\\.")[0] + ".barley")) {
					writer.write(result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});
		am.put("parser", args -> {
			try {
				String parserFile = SourceLoader.readSource(args[0].toString());
				String root = parserFile.split("\n")[0].split(" ")[1];
				String result = String.join("\n", List.of(parserFile.split("\n")).subList(1, parserFile.split("\n").length));
				String parser = "";
				parser += "-module(" + (args.length == 1 ? args[0].toString().split("\\.")[0] : args[1].toString()) + ").\n\n";
				parser += "global Pos = 0.\n"
					+ "global Size = 0.\n"
					+ "global Tokens = [].\n"
					+ "global Result = [].\n"
					+ "\n"
					+ "\n"
					+ "type(Tok) -> lists:nth(Tok, 0).\n"
					+ "text(Tok) -> lists:nth(Tok, 2).\n"
					+ "\n"
					+ "consume_in_bounds(P) when P < Size -> P.\n"
					+ "consume_in_bounds(P) -> Size - 1.\n"
					+ "\n"
					+ "consume_type(Token, Type) -> type(Token) == Type.\n"
					+ "\n"
					+ "get(RelativePos) ->\n"
					+ "    FinalPosition = Pos + RelativePos,\n"
					+ "    P = consume_in_bounds(FinalPosition),\n"
					+ "    lists:nth(Tokens, P).\n"
					+ "\n"
					+ "eval_match(C, T) when type(C) == T -> Pos = Pos + 1, true.\n"
					+ "\n"
					+ "eval_match(C, T) -> false.\n"
					+ "\n"
					+ "match(TokenType) ->\n"
					+ "    C = get(0),\n"
					+ "    eval_match(C, TokenType).\n\n";
				parser += "expr() -> " + root + "().\n\n";
				parser += result + "\n";
				parser += "make_parse() when match(eof) -> Result.\n"
					+ "make_parse() -> Expr = [expr()],\n"
					+ "                Result = Result + Expr,\n"
					+ "                make_parse().\n"
					+ "\n"
					+ "parse(Toks) ->\n"
					+ "    Pos = 0,\n"
					+ "    Tokens = Toks,\n"
					+ "    Size = barley:length(Toks),\n"
					+ "    Result = [],\n"
					+ "    make_parse().\n";
				try (FileWriter writer = new FileWriter(args[0].toString().split("\\.")[0] + ".barley")) {
					writer.write(parser);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ZuluAtom("ok");
		});

		put("amethyst", am);
	}

	private static void dumpMacros(String key, String value) {
		System.out.printf("Key: %s, Value: %s\n", key, value);
	}

	public static void init() {
		initBarley();
		initIo();
		initBts();
		initMath();
		initString();
		initStack();
		initTypes();
		initQueue();
		initHashmap();
		initMeasurement();
		initSignal();
		initCode();
		initBarleyUnit();
		initFile();
		// initSocket(); Deprecation
		initDist();
		initLists();
		initAmethyst();
		initInterface();
		initAnsi();
		initReflection();
		initBase();
		initXML();
		initMonty();
		initSQL();
		initFFI();
		initRef();
	}

	public static void initRef() {
		HashMap<String, Function> module = new HashMap<>();

		module.put("raw", args -> {
			if (args.length != 1) {
				throw new BarleyException("BadArg", "Expected atleast 1 argument to invoke");
			}

			ZuluValue first = args[0];
			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			return (ZuluValue) first.raw();
		});

		module.put("string", args -> {
			if (args.length != 1) {
				throw new BarleyException("BadArg", "Expected 1 argument to reference:string");
			}

			ZuluValue first = args[0];
			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			return new ZuluString(first.raw().toString());
		});

		module.put("number", args -> {
			if (args.length != 1) {
				throw new BarleyException("BadArg", "Expected 1 argument to reference:number");
			}

			ZuluValue first = args[0];
			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			var value = Double.valueOf(first.raw().toString());
			return new ZuluNumber(value);
		});


		module.put("boolean", args -> {
			if (args.length != 1) {
				throw new BarleyException("BadArg", "Expected 1 argument to reference:number");
			}

			ZuluValue first = args[0];
			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			var value = Boolean.valueOf(first.raw().toString());
			return new ZuluAtom(value.toString());
		});

		put("reference", module);
	}

	public static void initFFI() {
		HashMap<String, Function> module = new HashMap<>();

		module.put("load_class", args -> {
			Arguments.check(2, args.length);
			String libname = args[0].toString();
			String clasz_name = args[1].toString();
			Class cls = LibraryLoader.loadClass(libname, clasz_name);
			return new ZuluReference(cls);
		});

		module.put("invoke", args -> {
			if (args.length == 1) {
				throw new BarleyException("BadArg", "Expected atleast 2 argument to invoke");
			}

			ZuluValue first = args[0];
			Object second = args[1].raw();

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Method)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Method value, found " + ref);
			}

			Method method = (Method) ref;

			Object[] rest = new Object[args.length - 2];
			if (args.length - 2 >= 1) {
				for (int i = 2; i < args.length; i++) {
					rest[i - 2] = ((ZuluValue) args[i]).raw();
				}
			}

			Object result = null;
			try {
				result = method.invoke(second, rest);
			} catch (IllegalAccessException | InvocationTargetException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ZuluReference(result);
		});

		module.put("invoke_static", args -> {
			if (args.length == 1) {
				throw new BarleyException("BadArg", "Expected atleast 2 argument to invoke");
			}

			ZuluValue first = args[0];

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Method)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Method value, found " + ref);
			}

			Method method = (Method) ref;

			Object[] rest = new Object[args.length - 1];
			if (args.length - 1 >= 1) {
				for (int i = 1; i < args.length; i++) {
					rest[i - 1] = ((ZuluValue) args[i]).raw();
				}
			}

			Object result = null;
			try {
				result = method.invoke(null, rest);
			} catch (IllegalAccessException | InvocationTargetException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ZuluReference(result);
		});

		module.put("invoke_field", args -> {
			if (args.length == 1) {
				throw new BarleyException("BadArg", "Expected atleast 2 argument to invoke_field");
			}

			ZuluValue first = args[0];
			Object field = args[1].raw();

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Method)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Method value, found " + ref);
			}

			Method method = (Method) ref;

			Object[] rest = new Object[args.length - 2];
			if (args.length - 2 >= 1) {
				for (int i = 2; i < args.length; i++) {
					rest[i - 2] = ((ZuluValue) args[i]).raw();
				}
			}

			/*for (int i = 0; i < rest.length; i++) {
				rest[i] = rest[i].toString();
			}*/
			Object result = null;
			try {
				result = method.invoke(field, rest);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ZuluReference(result);
		});

		module.put("get_constructor", args -> {
			if (args.length == 1) {
				throw new BarleyException("BadArg", "Expected atleast 1 argument to invoke_field");
			}

			ZuluValue first = args[0];

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Class)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Class value, found " + ref);
			}

			Class<?> cls = (Class<?>) ref;

			// Extract remaining arguments to form the parameter types for the constructor
			ZuluValue[] rest = Arrays.copyOfRange(args, 1, args.length);
			Class<?>[] classes = new Class[rest.length];

			// Convert BarleyValues to their raw Class types
			for (int i = 0; i < rest.length; i++) {
				Object rawValue = rest[i].raw();

				if (!(rawValue instanceof Class)) {
					throw new BarleyException("BadArg", "Invalid parameter type at position " + (i + 2) + ", expected a Class value, found " + rawValue);
				}

				classes[i] = (Class<?>) rawValue;
			}

			try {
				Constructor<?> constructor = cls.getConstructor(classes);
				return new ZuluReference(constructor);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}

			return new ZuluReference(null);
		});

		module.put("new_instance", args -> {
			if (args.length == 1) {
				throw new BarleyException("BadArg", "Expected atleast 1 argument to new_instance");
			}

			ZuluValue first = args[0];

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Constructor)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Constructor value, found " + ref);
			}

			Constructor<?> constructor = (Constructor<?>) ref;

			Object[] rest = new Object[args.length - 1];
			if (args.length - 1 >= 1) {
				for (int i = 1; i < args.length; i++) {
					rest[i - 1] = args[i];
				}
			}

			// Convert BarleyValues to their raw Class types
			Object[] values = new Object[rest.length];
			for (int i = 0; i < rest.length; i++) {
				Object rawValue = ((ZuluValue) rest[i]).raw();
				values[i] = rawValue;
			}

			try {
				Object val = constructor.newInstance(values);
				return new ZuluReference(val);
			} catch (InstantiationException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ZuluReference(null);
		});

		module.put("get_field", args -> {

			Arguments.check(2, args.length);
			ZuluValue first = args[0];
			String field_name = args[1].toString();

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Class)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Class value");
			}

			Class<?> cls = (Class) ref;
			Field field = null;
			try {
				field = cls.getField(field_name);
				return new ZuluReference(field.get(null));
			} catch (NoSuchFieldException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		});

		module.put("get_method_at", args -> {
			Arguments.check(3, args.length);
			ZuluValue first = args[0];
			String method_name = args[1].toString();
			String index = args[2].raw().toString();
			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Class)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Class value");
			}

			Class cls = (Class) ref;

			var methods = Stream
				.of(cls.getMethods())
				.filter(m -> {
					return m.getName().equals(method_name); // && m.getParameterCount() == types.length;
				}).collect(Collectors.toList());

			if (methods.isEmpty()) {
				System.err.println("Fatal");
				System.exit(0);
			}

			int i = Integer.parseInt(index);
			return new ZuluReference(methods.get(i));
		});

		module.put("get_method", args -> {
			Arguments.check(2, args.length);
			ZuluValue first = args[0];
			String method_name = args[1].toString();

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Class)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Class value");
			}
			Class cls = (Class) ref;

			var methods = Stream
				.of(cls.getMethods())
				.filter(m -> {
					return m.getName().equals(method_name); // && m.getParameterCount() == types.length;
				}).collect(Collectors.toList());

			if (methods.isEmpty()) {
				System.err.println("Fatal");
				System.exit(0);
			}

			return new ZuluReference(methods.get(0));
		});

		module.put("construct", args -> {
			Arguments.check(1, args.length);
			ZuluValue first = args[0];

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object ref = first.raw();
			if (!(ref instanceof Class)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Class value");
			}

			Class cls = (Class) ref;

			Object[] rest = new Object[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				rest[i - 1] = args[i];
			}

			// Clean up the arguments to so that they can be understood by java
			/*for (int i = 0; i < rest.length; i++) {
				Object c = rest[i];
				c = switch (c) {
					case BarleyNull d -> null;
					case BarleyList l -> l.getList().toArray();
					case BarleyNumber n -> n.asInteger().intValue();
					case BarleyString s -> s.toString();
					default -> c;
				};
				rest[i] = c;
			}*/
			Object instance = null;
			try {
				instance = cls.getDeclaredConstructor().newInstance(rest);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}

			return new ZuluReference(instance);
		});

		put("ffi", module);
	}

	public static void initSQL() {
		HashMap<String, Function> module = new HashMap<>();
		module.put("connect_db_driver", args -> {
			Arguments.check(2, args.length);

			String driver = args[0].toString();
			String db_file = args[1].toString();

			try {
				Connection connection = DriverManager.getConnection("jdbc:" + driver + ":" + db_file);
				return new ZuluReference(connection);
			} catch (SQLException ex) {
				Logger.getLogger(Modules.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ZuluAtom("error");
		});

		module.put("query", args -> {
			Arguments.check(2, args.length);
			ZuluValue first = args[0];
			String query = args[1].toString();

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			ZuluReference connection_ref = (ZuluReference) first;
			Object ref = connection_ref.raw();

			if (!(ref instanceof Connection)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a sql connection");
			}

			Connection c = (Connection) ref;
			Statement statement;
			ResultSet rs;
			try {
				statement = c.createStatement();
				rs = statement.executeQuery(query.toUpperCase());
			} catch (SQLException ex) {
				throw new BarleyException("Runtime", "sql query error");
			}

			try {
				var rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				LinkedList<ZuluValue> results = new LinkedList<>();
				while (rs.next()) {
					LinkedList<ZuluValue> internal = new LinkedList<>();
					for (int i = 1; i <= count; i++) {
						String field_name = rsmd.getColumnName(i);
						Object obj = rs.getObject(field_name);
						internal.add(new ZuluReference(obj));
					}
					results.add(new ZuluList(internal));
				}

				return new ZuluList(results);
			} catch (SQLException ex) {
				throw new BarleyException("Runtime", "sql column error");
			}
		});
		put("sql", module);
	}

	public static void initXML() {
		HashMap<String, Function> module = new HashMap<>();

		module.put("stringify", args -> {
			Arguments.check(1, args.length);
			ZuluValue first = args[0];

			if (!(first instanceof ZuluReference)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference value");
			}

			Object o = ((ZuluReference) first).raw();
			if (!(o instanceof ZuluXML)) {
				throw new BarleyException("BadArg", "Invalid value provide for param `1`, expected a Reference to an XML Object value");
			}

			ZuluXML xml = (ZuluXML) o;

			String str = xml.getFormatted();
			if (str == null) {
				return new ZuluAtom("error");
			}
			return new ZuluString(str);
		});

		put("xml", module);
	}

	public static void initMonty() {
		HashMap<String, Function> monty = new HashMap<>();

		monty.put("dispose_on_close", args -> new ZuluNumber(JFrame.DISPOSE_ON_CLOSE));
		monty.put("do_nothing_on_close", args -> new ZuluNumber(JFrame.DO_NOTHING_ON_CLOSE));
		monty.put("exit_on_close", args -> new ZuluNumber(JFrame.EXIT_ON_CLOSE));
		monty.put("hide_on_close", args -> new ZuluNumber(JFrame.HIDE_ON_CLOSE));

		// Swing constants
		monty.put("swing_bottom", args -> new ZuluNumber(SwingConstants.BOTTOM));
		monty.put("swing_center", args -> new ZuluNumber(SwingConstants.CENTER));
		monty.put("swing_east", args -> new ZuluNumber(SwingConstants.EAST));
		monty.put("swing_horiz", args -> new ZuluNumber(SwingConstants.HORIZONTAL));
		monty.put("swing_leading", args -> new ZuluNumber(SwingConstants.LEADING));
		monty.put("swing_left", args -> new ZuluNumber(SwingConstants.LEFT));
		monty.put("swing_next", args -> new ZuluNumber(SwingConstants.NEXT));
		monty.put("swing_north", args -> new ZuluNumber(SwingConstants.NORTH));
		monty.put("swing_north_east", args -> new ZuluNumber(SwingConstants.NORTH_EAST));
		monty.put("swing_north_west", args -> new ZuluNumber(SwingConstants.NORTH_WEST));
		monty.put("swing_previous", args -> new ZuluNumber(SwingConstants.PREVIOUS));
		monty.put("swing_right", args -> new ZuluNumber(SwingConstants.RIGHT));
		monty.put("swing_south", args -> new ZuluNumber(SwingConstants.SOUTH));
		monty.put("swing_south_east", args -> new ZuluNumber(SwingConstants.SOUTH_EAST));
		monty.put("swing_south_west", args -> new ZuluNumber(SwingConstants.SOUTH_WEST));
		monty.put("swing_top", args -> new ZuluNumber(SwingConstants.TOP));
		monty.put("swing_trailing", args -> new ZuluNumber(SwingConstants.TRAILING));
		monty.put("swing_vertical", args -> new ZuluNumber(SwingConstants.VERTICAL));
		monty.put("swing_west", args -> new ZuluNumber(SwingConstants.WEST));

		// Layout managers constants
		monty.put("AFTER_LAST_LINE".toLowerCase(), args -> new ZuluString(BorderLayout.AFTER_LAST_LINE));
		monty.put("AFTER_LINE_ENDS".toLowerCase(), args -> new ZuluString(BorderLayout.AFTER_LINE_ENDS));
		monty.put("BEFORE_FIRST_LINE".toLowerCase(), args -> new ZuluString(BorderLayout.BEFORE_FIRST_LINE));
		monty.put("BEFORE_LINE_BEGINS".toLowerCase(), args -> new ZuluString(BorderLayout.BEFORE_LINE_BEGINS));
		monty.put("CENTER".toLowerCase(), args -> new ZuluString(BorderLayout.CENTER));
		monty.put("EAST".toLowerCase(), args -> new ZuluString(BorderLayout.EAST));
		monty.put("LINE_END".toLowerCase(), args -> new ZuluString(BorderLayout.LINE_END));
		monty.put("LINE_START".toLowerCase(), args -> new ZuluString(BorderLayout.LINE_START));
		monty.put("NORTH".toLowerCase(), args -> new ZuluString(BorderLayout.NORTH));
		monty.put("PAGE_END.toLowerCase()", args -> new ZuluString(BorderLayout.PAGE_END));
		monty.put("PAGE_START".toLowerCase(), args -> new ZuluString(BorderLayout.PAGE_START));
		monty.put("SOUTH".toLowerCase(), args -> new ZuluString(BorderLayout.SOUTH));
		monty.put("WEST".toLowerCase(), args -> new ZuluString(BorderLayout.WEST));

		monty.put("instantiate_window", Monty::Window);
		monty.put("set_border_layout", Monty::BorderLayout);
		monty.put("set_flow_layout", Monty::FlowLayout);
		monty.put("set_grid_layout", Monty::GridLayout);
		monty.put("set_visible", Monty::SetVisible);
		monty.put("set_resizable", Monty::SetResizable);
		monty.put("new_panel", Monty::Panel);
		monty.put("text_label", Monty::Text);
		monty.put("set_size", Monty::SetSize);
		monty.put("center", Monty::Center);
		monty.put("action_button", Monty::ActionButton);
		monty.put("button", Monty::Button);
		monty.put("pack_awt", Monty::Pack);
		monty.put("slider", Monty::Slider);
		monty.put("check_box", Monty::CheckBox);
		monty.put("clear_frame", Monty::ClearFrame);
		monty.put("color", args -> new ZuluReference(new Color(args[0].asInteger().intValue(), args[1].asInteger().intValue(), args[2].asInteger().intValue(), args[3].asInteger().intValue())));
		monty.put("image_render", Monty::Image);
		monty.put("progress_bar", Monty::ProgressBar);
		monty.put("step_bar", Monty::StepBar);

		put("monty", monty);
	}

	private static void initBase() {
		HashMap<String, Function> base = new HashMap<>();

		base.put("encode", Modules::base64encode);
		base.put("decode", Modules::base64decode);
		base.put("decode_to_string", Modules::base64encodeToString);

		put("base", base);
	}

	private static void initReflection() {
//        HashMap<String, Function> ref = new HashMap<>();
//
//        ref.put("int", args -> new BarleyReference(int.class));
//        ref.put("byte", args -> new BarleyReference(byte.class));
//        ref.put("float", args -> new BarleyReference(float.class));
//        ref.put("short", args -> new BarleyReference(short.class));
//        ref.put("long", args -> new BarleyReference(long.class));
//        ref.put("double", args -> new BarleyReference(double.class));
//        ref.put("int_arr", args -> new BarleyReference(int[].class));
//        ref.put("byte_arr", args -> new BarleyReference(byte[].class));
//        ref.put("float_arr", args -> new BarleyReference(float[].class));
//        ref.put("short_arr", args -> new BarleyReference(short[].class));
//        ref.put("long_arr", args -> new BarleyReference(long[].class));
//        ref.put("double_arr", args -> new BarleyReference(double[].class));
//
//        ref.put("class", args -> {
//            Arguments.check(1, args.length);
//            try {
//                return new BarleyReference(Class.forName(args[0].toString()));
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            return new BarleyAtom("error");
//        });
//
//        ref.put("declared_fields", args -> {
//            Arguments.check(1, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Field[] fields = cl.getDeclaredFields();
//            LinkedList<BarleyValue> result = new LinkedList<>();
//            for (Field field : fields) {
//                result.add(new BarleyReference(field));
//            }
//            return new BarleyList(result);
//        });
//
//        ref.put("declared_field", args -> {
//            Arguments.check(2, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            try {
//                return new BarleyReference(cl.getDeclaredField(args[1].toString()));
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//            return new BarleyAtom("error");
//        });
//        ref.put("instance", args -> {
//            Arguments.checkAtLeast(1, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Object[] const_args = List.of(args).subList(1, args.length).toArray();
//            return instance(cl.getConstructors(), const_args);
//        });
//        ref.put("fields", args -> {
//            Arguments.check(1, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Field[] fields = cl.getFields();
//            LinkedList<BarleyValue> result = new LinkedList<>();
//            for (Field field : fields) {
//                result.add(new BarleyReference(field));
//            }
//            return new BarleyList(result);
//        });
//
//        ref.put("field", args -> {
//            Arguments.check(2, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            try {
//                return new BarleyReference(cl.getField(args[1].toString()));
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//            return new BarleyAtom("error");
//        });
//
//        ref.put("declared_methods", args -> {
//            Arguments.check(1, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Method[] methods = cl.getDeclaredMethods();
//            LinkedList<BarleyValue> result = new LinkedList<>();
//            for (Method field : methods) {
//                result.add(new BarleyReference(field));
//            }
//            return new BarleyList(result);
//        });
//
//        ref.put("methods", args -> {
//            Arguments.check(1, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Method[] methods = cl.getMethods();
//            LinkedList<BarleyValue> result = new LinkedList<>();
//            for (Method field : methods) {
//                result.add(new BarleyReference(field));
//            }
//            return new BarleyList(result);
//        });
//
//        ref.put("method", args -> {
//            Arguments.check(2, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            try {
//                return new BarleyReference(cl.getMethod(args[1].toString()));
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            return new BarleyAtom("error");
//        });
//
//        ref.put("enclosing_method", args -> {
//            Arguments.check(1, args.length);
//            Class<?> cl = (((BarleyReference) args[0]).getRef()).getClass();
//            return new BarleyReference(cl.getEnclosingMethod());
//        });
//
//        ref.put("modifiers", args -> {
//            Arguments.check(1, args.length);
//            Method method = (Method) ((BarleyReference) args[0]).getRef();
//            return new BarleyNumber(method.getModifiers());
//        });
//        ref.put("is_public", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isFinal(args[0].asInteger().intValue())));
//        });
//        ref.put("is_private", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isPrivate(args[0].asInteger().intValue())));
//        });
//        ref.put("is_protected", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isProtected(args[0].asInteger().intValue())));
//        });
//        ref.put("is_transient", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isTransient(args[0].asInteger().intValue())));
//        });
//        ref.put("is_synchronized", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isSynchronized(args[0].asInteger().intValue())));
//        });
//        ref.put("is_native", args -> {
//            Arguments.check(1, args.length);
//            return new BarleyAtom(String.valueOf(Modifier.isNative(args[0].asInteger().intValue())));
//        });
//
//        ref.put("return_type", args -> {
//            Arguments.check(1, args.length);
//            Method method = (Method) ((BarleyReference) args[0]).getRef();
//            return new BarleyReference(method.getReturnType());
//        });
//
//        ref.put("generic_return_type", args -> {
//            Arguments.check(1, args.length);
//            Method method = (Method) ((BarleyReference) args[0]).getRef();
//            return new BarleyReference(method.getGenericReturnType());
//        });
//
//        ref.put("accessible", args -> {
//            Arguments.check(2, args.length);
//            Method method = (Method) ((BarleyReference) args[0]).getRef();
//            method.setAccessible(Boolean.parseBoolean(args[1].toString()));
//            return args[1];
//        });
//
//        ref.put("invoke", args -> {
//            Arguments.checkAtLeast(2, args.length);
//            Class<?> cl = (Class<?>) ((BarleyReference) args[0]).getRef();
//            Method method = (Method) ((BarleyReference) args[1]).getRef();
//            Object[] ar = List.of(args).subList(2, args.length).toArray();
//            try {
//                return new BarleyReference(method.invoke(cl, ar));
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            return new BarleyAtom("error");
//        });

		new Reflection().inject();
	}

	private static void initAnsi() {
		HashMap<String, Function> ansi = new HashMap<>();

		ansi.put("reset", args -> new ZuluString(ANSI_RESET));
		ansi.put("red", args -> new ZuluString(ANSI_RED));
		ansi.put("red_bg", args -> new ZuluString(ANSI_RED_BACKGROUND));
		ansi.put("blue", args -> new ZuluString(ANSI_BLUE));
		ansi.put("blue_bg", args -> new ZuluString(ANSI_BLUE_BACKGROUND));
		ansi.put("purple", args -> new ZuluString(ANSI_PURPLE));
		ansi.put("purple_bg", args -> new ZuluString(ANSI_PURPLE_BACKGROUND));
		ansi.put("yellow", args -> new ZuluString(ANSI_YELLOW));
		ansi.put("yellow_bg", args -> new ZuluString(ANSI_YELLOW_BACKGROUND));
		ansi.put("black", args -> new ZuluString(ANSI_BLACK));
		ansi.put("white", args -> new ZuluString(ANSI_WHITE));
		ansi.put("white_bg", args -> new ZuluString(ANSI_WHITE_BACKGROUND));

		put("ansi", ansi);
	}

	private static void initInterface() {
		HashMap<String, Function> inter = new HashMap<>();
		inter.put("vk_up", args -> new ZuluNumber(KeyEvent.VK_UP));
		inter.put("vk_down", args -> new ZuluNumber(KeyEvent.VK_DOWN));
		inter.put("vk_left", args -> new ZuluNumber(KeyEvent.VK_LEFT));
		inter.put("vk_right", args -> new ZuluNumber(KeyEvent.VK_RIGHT));
		inter.put("vk_fire", args -> new ZuluNumber(KeyEvent.VK_ENTER));
		inter.put("vk_escape", args -> new ZuluNumber(KeyEvent.VK_ESCAPE));

		inter.put("window", new CreateWindow());
		inter.put("prompt", new Prompt());
		inter.put("key_pressed", new KeyPressed());
		inter.put("mouse_hover", new MouseHover());

		inter.put("line", args -> {
			line(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("oval", args -> {
			oval(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("foval", args -> {
			foval(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("rect", args -> {
			rect(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("frect", args -> {
			frect(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("clip", args -> {
			clip(args[0].asInteger().intValue(),
				args[1].asInteger().intValue(),
				args[2].asInteger().intValue(),
				args[3].asInteger().intValue());
			return new ZuluAtom("ok");
		});
		inter.put("string", new DrawString());
		inter.put("color", new SetColor());
		inter.put("repaint", new Repaint());

		put("interface", inter);
	}

	private static void line(int x1, int y1, int x2, int y2) {
		graphics.drawLine(x1, y1, x2, y2);
	}

	private static void oval(int x, int y, int w, int h) {
		graphics.drawOval(x, y, w, h);
	}

	private static void foval(int x, int y, int w, int h) {
		graphics.fillOval(x, y, w, h);
	}

	private static void rect(int x, int y, int w, int h) {
		graphics.drawRect(x, y, w, h);
	}

	private static void frect(int x, int y, int w, int h) {
		graphics.fillRect(x, y, w, h);
	}

	private static void clip(int x, int y, int w, int h) {
		graphics.setClip(x, y, w, h);
	}

	private static void initLists() {
		HashMap<String, Function> lists = new HashMap<>();

		lists.put("map", args -> {
			Arguments.check(2, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			if (!(args[1] instanceof ZuluList)) {
				throw new BarleyException("BadArg", "Expected LIST, got " + args[1]);
			}
			ZuluList list = (ZuluList) args[1];
			LinkedList<ZuluValue> result = new LinkedList<>();
			for (ZuluValue val : list.getList()) {
				result.add(fun.execute(val));
			}
			return new ZuluList(result);
		});

		lists.put("filter", args -> {
			Arguments.check(2, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			ZuluList list = (ZuluList) args[1];
			LinkedList<ZuluValue> result = new LinkedList<>();
			for (ZuluValue val : list.getList()) {
				if (fun.execute(val).toString().equals("true")) {
					result.add(val);
				}
			}
			return new ZuluList(result);
		});

		lists.put("reduce", args -> {
			Arguments.check(3, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			ZuluList list = (ZuluList) args[1];
			ZuluValue acc = args[2];
			for (ZuluValue val : list.getList()) {
				acc = fun.execute(val, acc);
			}
			return acc;
		});

		lists.put("append", args -> {
			Arguments.check(2, args.length);
			ZuluList list = (ZuluList) args[0];
			LinkedList<ZuluValue> l = new LinkedList<>(list.getList());
			l.add(args[1]);
			return new ZuluList(l);

		});

		lists.put("max", args -> {
			Arguments.check(1, args.length);
			LinkedList<ZuluValue> list = ((ZuluList) args[0]).getList();
			ArrayList<Integer> ints = new ArrayList<>();
			for (ZuluValue val : list) {
				ints.add(val.asInteger().intValue());
			}
			Integer[] arr1 = ints.toArray(new Integer[]{});
			int[] arr = new int[arr1.length];
			for (int i = 0; i < arr1.length; i++) {
				arr[i] = arr1[i];
			}
			return new ZuluNumber(Arrays.stream(arr).max().getAsInt());
		});

		lists.put("min", args -> {
			Arguments.check(1, args.length);
			LinkedList<ZuluValue> list = ((ZuluList) args[0]).getList();
			ArrayList<Integer> ints = new ArrayList<>();
			for (ZuluValue val : list) {
				ints.add(val.asInteger().intValue());
			}
			Integer[] arr1 = ints.toArray(new Integer[]{});
			int[] arr = new int[arr1.length];
			for (int i = 0; i < arr1.length; i++) {
				arr[i] = arr1[i];
			}
			return new ZuluNumber(Arrays.stream(arr).min().getAsInt());
		});

		lists.put("concat", args -> {
			Arguments.check(1, args.length);
			ZuluList list = (ZuluList) args[0];
			StringBuilder acc = new StringBuilder();
			for (ZuluValue val : list.getList()) {
				acc.append(val.toString());
			}
			return new ZuluString(acc.toString());
		});

		lists.put("duplicate", args -> {
			Arguments.check(2, args.length);
			LinkedList<ZuluValue> result = new LinkedList<>();
			ZuluValue obj = args[0];
			int iteration = args[1].asFloat().intValue();
			for (int i = 0; i < iteration; i++) {
				result.add(obj);
			}
			return new ZuluList(result);
		});

		lists.put("seq", args -> {
			Arguments.check(2, args.length);
			LinkedList<ZuluValue> result = new LinkedList<>();
			ZuluValue obj = args[0];
			int iteration = args[1].asFloat().intValue();
			for (int i = 0; i < iteration; i++) {
				result.add(obj);
			}
			return new ZuluList(result);
		});

		lists.put("foreach", args -> {
			Arguments.check(2, args.length);
			ZuluFunction fun = (ZuluFunction) args[0];
			ZuluList list = (ZuluList) args[1];
			for (ZuluValue val : list.getList()) {
				fun.execute(val);
			}
			return new ZuluAtom("ok");
		});

		lists.put("last", args -> {
			Arguments.check(1, args.length);
			ZuluList list = (ZuluList) args[0];
			return list.getList().get(list.getList().size() - 1);
		});

		lists.put("nth", args -> {
			Arguments.check(2, args.length);
			ZuluList list = (ZuluList) args[0];
			int nth = args[1].asInteger().intValue();
			try {
				return list.getList().get(nth);
			} catch (IndexOutOfBoundsException ex) {
				return new ZuluAtom("end_of_list");
			}
		});

		lists.put("reverse", args -> {
			Arguments.check(1, args.length);
			LinkedList<ZuluValue> list = ((ZuluList) args[0]).getList();
			Collections.reverse(list);
			return new ZuluList(list);
		});

		lists.put("sublist", args -> {
			ZuluList list = (ZuluList) args[0];
			int from = args[1].asInteger().intValue();
			int to = args[2].asInteger().intValue();
			List<ZuluValue> subd = list.getList().subList(from, to);
			LinkedList<ZuluValue> result = new LinkedList<>(subd);
			return new ZuluList(result);
		});

		lists.put("dimension", args -> {
			int acc = 0;
			for (int i = 0; i < args.length; i++) {
				acc += args[i].asInteger().intValue();
			}
			LinkedList<ZuluValue> arr = new LinkedList<>();
			for (int i = 0; acc > i; i++) {
				arr.add(new ZuluAtom("null"));
			}
			return new ZuluList(arr);
		});

		put("list", lists);
	}

	private static String microsToSeconds(long micros) {
		return new DecimalFormat("#0.0000").format(micros / 1000d / 1000d) + " sec";
	}

	static byte[] toPrimitives(Byte[] oBytes) {
		byte[] bytes = new byte[oBytes.length];
		for (int i = 0; i < oBytes.length; i++) {
			bytes[i] = oBytes[i];
		}
		return bytes;
	}

	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static void put(String name, HashMap<String, Function> methods) {
		modules.put(name, methods);
	}

	public static HashMap<String, Function> get(String name) {
		return modules.get(name);
	}

	public static boolean isExists(String name) {
		return modules.containsKey(name);
	}

	private static class runTests implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			HashMap<String, Function> methods = modules.get(args[0].toString());
			List<TestInfo> tests = methods.entrySet().stream()
				.filter(e -> e.getKey().toLowerCase().startsWith("test"))
				.map(e -> runTest(e.getKey(), e.getValue()))
				.collect(Collectors.toList());

			int failures = 0;
			long summaryTime = 0;
			final StringBuilder result = new StringBuilder();
			for (TestInfo test : tests) {
				if (!test.isPassed) {
					failures++;
				}
				summaryTime += test.elapsedTimeInMicros;
				result.append("\n");
				result.append(test.info());
			}
			result.append("\n");
			result.append(String.format("Tests run: %d, Failures: %d, Time elapsed: %s",
				tests.size(), failures,
				microsToSeconds(summaryTime)));
			return new ZuluString(result.toString());
		}

		private TestInfo runTest(String name, Function f) {
			final long startTime = System.nanoTime();
			boolean isSuccessfull;
			String failureDescription;
			try {
				f.execute();
				isSuccessfull = true;
				failureDescription = "";
			} catch (BUnitAssertionException oae) {
				isSuccessfull = false;
				failureDescription = oae.getText();
			}
			final long elapsedTime = System.nanoTime() - startTime;
			return new TestInfo(name, isSuccessfull, failureDescription, elapsedTime / 1000);
		}
	}

	private static class BUnitAssertionException extends BarleyException {

		public BUnitAssertionException(String message) {
			super("BadTest", message);
		}
	}

	private static class TestInfo {

		String name;
		boolean isPassed;
		String failureDescription;
		long elapsedTimeInMicros;

		public TestInfo(String name, boolean isPassed, String failureDescription, long elapsedTimeInMicros) {
			this.name = name;
			this.isPassed = isPassed;
			this.failureDescription = failureDescription;
			this.elapsedTimeInMicros = elapsedTimeInMicros;
		}

		public String info() {
			return String.format("%s [%s]\n%sElapsed: %s\n",
				name,
				isPassed ? "passed" : "FAILED",
				isPassed ? "" : (failureDescription + "\n"),
				microsToSeconds(elapsedTimeInMicros)
			);
		}
	}

	private static class CanvasPanel extends JPanel {

		public CanvasPanel(int width, int height) {
			setPreferredSize(new Dimension(width, height));
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graphics = img.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			setFocusable(true);
			requestFocus();
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					lastKey = new ZuluNumber(e.getKeyCode());
				}

				@Override
				public void keyReleased(KeyEvent e) {
					lastKey = new ZuluNumber(-1);
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					mouseHover.set(0, new ZuluNumber(e.getX()));
					mouseHover.set(1, new ZuluNumber(e.getY()));
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, null);
		}
	}

	private static class CreateWindow implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			String title = "";
			int width = 640;
			int height = 480;
			switch (args.length) {
				case 1:
					title = args[0].toString();
					break;
				case 2:
					width = args[0].asInteger().intValue();
					height = args[1].asFloat().intValue();
					break;
				case 3:
					title = args[0].toString();
					width = args[1].asInteger().intValue();
					height = args[2].asInteger().intValue();
					break;
			}
			panel = new CanvasPanel(width, height);

			frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(panel);
			frame.pack();
			frame.setVisible(true);
			return new ZuluNumber(0);
		}
	}

	private static class KeyPressed implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			return lastKey;
		}
	}

	private static class MouseHover implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			return mouseHover;
		}
	}

	private static class DrawString implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			Arguments.check(3, args.length);
			int x = args[1].asInteger().intValue();
			int y = args[2].asInteger().intValue();
			graphics.drawString(args[0].toString(), x, y);
			return new ZuluNumber(0);
		}
	}

	private static class Prompt implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			final String v = JOptionPane.showInputDialog(args[0].toString());
			return new ZuluString(v == null ? "0" : v);
		}
	}

	private static class Repaint implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			panel.invalidate();
			panel.repaint();
			return new ZuluNumber(0);
		}
	}

	private static class SetColor implements Function {

		@Override
		public ZuluValue execute(ZuluValue... args) {
			if (args.length == 1) {
				graphics.setColor(new Color(args[0].asInteger().intValue()));
				return new ZuluNumber(0);
			}
			int r = args[0].asInteger().intValue();
			int g = args[1].asInteger().intValue();
			int b = args[2].asInteger().intValue();
			graphics.setColor(new Color(r, g, b));
			return new ZuluNumber(0);
		}

	}

	static class Results {

		private int tabs = 0;
		private int spaces = 0;

		public void incTabCount() {
			++tabs;
		}

		public void incSpaceCount() {
			++spaces;
		}

		public int getTabCount() {
			return tabs;
		}

		public int getSpaceCount() {
			return spaces;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("tabs: ");
			sb.append(tabs);
			sb.append("\nspaces: ");
			sb.append(spaces);

			return sb.toString();
		}
	}

	private static int TYPE_URL_SAFE = 8;

	private static ZuluValue base64encode(ZuluValue... args) {
		Arguments.checkOrOr(1, 2, args.length);
		byte[] bytes = getEncoder(args).encode(getInputToEncode(args));
		LinkedList<ZuluValue> result = new LinkedList<>();
		for (byte b : bytes) {
			result.add(new ZuluNumber(b));
		}
		return new ZuluList(result);
	}

	private static ZuluValue base64encodeToString(ZuluValue... args) {
		Arguments.checkOrOr(1, 2, args.length);
		return new ZuluString(getEncoder(args).encodeToString(getInputToEncode(args)));
	}

	private static ZuluValue base64decode(ZuluValue... args) {
		Arguments.checkOrOr(1, 2, args.length);
		final Base64.Decoder decoder = getDecoder(args);
		final byte[] result;
		if (args[0] instanceof ZuluList s) {
			byte[] ar = new byte[s.getList().size()];
			for (int i = 0; i < ar.length; i++) {
				ar[i] = s.getList().get(i).asInteger().byteValue();
			}
			result = decoder.decode(ar);
		} else {
			result = decoder.decode(args[0].toString());
			ZuluValue[] ints = new ZuluValue[result.length];
			for (int i = 0; i < result.length; i++) {
				ints[i] = new ZuluNumber(result[i]);
			}
			return new ZuluList(ints);
		}
		return null;
	}

	private static byte[] getInputToEncode(ZuluValue[] args) {
		byte[] input;
		if (args[0] instanceof ZuluList a) {
			input = new byte[a.getList().size()];
			for (int i = 0; i < a.getList().size(); i++) {
				input[i] = a.getList().get(i).asInteger().byteValue();
			}
		} else {
			try {
				input = args[0].toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException ex) {
				input = args[0].toString().getBytes();
			}
		}
		return input;
	}

	private static Base64.Encoder getEncoder(ZuluValue[] args) {
		if (args.length == 2 && args[1].asInteger().intValue() == TYPE_URL_SAFE) {
			return Base64.getUrlEncoder();
		}
		return Base64.getEncoder();
	}

	private static Base64.Decoder getDecoder(ZuluValue[] args) {
		if (args.length == 2 && args[1].asInteger().intValue() == TYPE_URL_SAFE) {
			return Base64.getUrlDecoder();
		}
		return Base64.getDecoder();
	}

	/**
	 * Iterate the file once, checking for all desired characters, and store in
	 * the Results object
	 */
	private static Results countInStream(String s) throws IOException {
		InputStream is = new ByteArrayInputStream(s.getBytes("UTF8"));
		return countInStream(is);
	}

	private static Results countInStream(InputStream is) throws IOException {
		// create results
		Results res = new Results();
		try {
			byte[] c = new byte[1024];

			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					// see if we have a tab
					if (c[i] == '\t') {
						res.incTabCount();
					}

					// see if we have a space
					if (c[i] == ' ') {
						res.incSpaceCount();
					}

				}
			}
		} finally {
		}

		return res;
	}

	public static boolean containsKey(Object key) {
		return modules.containsKey(key);
	}
}
