package com.erlava;

import com.erlava.runtime.Table;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.Modules;
import com.erlava.utils.BarleyException;
import com.erlava.utils.Handler;
import com.erlava.utils.SourceLoader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {
		ArgParser argp = new ArgParser(args);
		Config conf = new Config();
		conf.setProgram("Erlava");
		conf.setVersion(Handler.RUNTIME_VERSION);
		conf.parse(argp);

		AnsiConsole.out().print("");
		Modules.init();

		if (conf.isRepl()) {
			Table.set("Args", new BarleyList());
			Handler.console();
		} else if (conf.isTest()) {
			int argsLength = 1;
			LinkedList<BarleyValue> argsc = new LinkedList<>();
			for (String arg : conf.getFiles()) {
				argsc.add(new BarleyString(arg));
			}
			Table.set("ARGS", new BarleyList(argsc));
			Handler.tests();
			return;
		} else if (!conf.getEntry().isBlank()) {
			Handler.entry(conf.getEntry(), conf.getEntry_module());

		} else if (conf.hasFiles()) {
			for (String file : conf.getFiles()) {
				String[] dotParts = file.split("\\.");
				if (dotParts[dotParts.length - 1].equals("app")) {
					Modules.get("dist").get("app").execute(new BarleyString(file));
				} else {
					try {
						Handler.handle(SourceLoader.readSource(file), false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

			/*
		String file = args[0];
		if (file.equals("-entry")) {
			argsLength = 4;
			LinkedList<BarleyValue> argsc = new LinkedList<>();
			/*for (String arg : List.of(args).subList(argsLength, args.length)) {
                argsc.add(new BarleyString(arg));
            }*/
			//Table.set("ARGS", new BarleyList(argsc));
			/*Handler.entry(args[1], args[2]);
			return;
		}
		
		LinkedList<BarleyValue> argsc = new LinkedList<>();
		for (String arg : List.of(args).subList(argsLength, args.length)) {
			argsc.add(new BarleyString(arg));
		}
		Table.set("ARGS", new BarleyList(argsc));
		String[] dotParts = file.split("\\.");
		if (dotParts[dotParts.length - 1].equals("app")) {
			Modules.get("dist").get("app").execute(new BarleyString(file));
		} else {
			argsLength = 1;
			try {
				Handler.handle(SourceLoader.readSource(file), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}

	public static void error(String type, String text, int line, String current) {
		throw new BarleyException(type, text + "\n    at line " + line + "\n      when current line:\n            " + current);
	}
}
