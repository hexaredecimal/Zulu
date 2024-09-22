package com.erlava;

import com.erlava.runtime.Table;
import com.erlava.runtime.BarleyValue;
import com.erlava.runtime.BarleyString;
import com.erlava.runtime.BarleyList;
import com.erlava.runtime.LibraryLoader;
import com.erlava.runtime.Modules;
import com.erlava.utils.BarleyException;
import com.erlava.utils.FileUtils;
import com.erlava.utils.Handler;
import com.erlava.utils.SourceLoader;

import java.io.IOException;
import java.util.LinkedList;



public class Main {

	public static void main(String[] args) throws IOException {
		
		//Test.run();
		//System.exit(0);
		
		ArgParser argp = new ArgParser(args);
		Config conf = new Config();
		conf.setProgram("Erlava");
		conf.setVersion(Handler.RUNTIME_VERSION);
		conf.parse(argp);

		LibraryLoader.init();
		Modules.init();

		if (conf.isRepl()) {
			Table.set("Args", new BarleyList());
			Handler.console();
		} else if (conf.isTest()) {
			LinkedList<BarleyValue> argsc = new LinkedList<>();
			for (String arg : conf.getFiles()) {
				argsc.add(new BarleyString(arg));
			}
			Table.set("Args", new BarleyList(argsc));
			Handler.tests();
		} else if (!conf.getEntry().isBlank()) {
			FileUtils.expectExtention(conf.getEntry(), "lava");
			Handler.entry(conf.getEntry(), conf.getEntry_module());
		} else if (conf.hasFiles()) {
			for (String file : conf.getFiles()) {
				String ext = FileUtils.getExtension(file);
				if (ext.equals("app")) {
					Modules.get("dist").get("app").execute(new BarleyString(file));
				} else {
					try {
						FileUtils.expectExtention(file, "lava");
						Handler.handle(SourceLoader.readSource(file), false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("error: No input provided. Type `help` for usage information");
		}
	}

	public static void error(String type, String text, int line, String current) {
		StringBuilder sb = new StringBuilder();
		sb
			.append(text.indent(0))
			.append(String.format("line %d: ", line).indent(0))
			.append(current.indent(2));
		throw new BarleyException(type, sb.toString());
	}
}
