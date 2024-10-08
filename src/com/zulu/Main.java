package com.zulu;

import com.zulu.runtime.Table;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.LibraryLoader;
import com.zulu.runtime.Modules;
import com.zulu.utils.ZuluException;
import com.zulu.utils.FileUtils;
import com.zulu.utils.Handler;
import com.zulu.utils.SourceLoader;

import java.io.IOException;
import java.util.LinkedList;
import com.zulu.runtime.ZuluValue;

public class Main {

	public static void main(String[] args) throws IOException {
		ArgParser argp = new ArgParser(args);
		Config conf = new Config();
		conf.setProgram("zulu");
		conf.setVersion(Handler.RUNTIME_VERSION);
		conf.parse(argp);

		LibraryLoader.init();
		Modules.init();

		if (conf.isRepl()) {
			Table.set("Args", new ZuluList());
			Handler.console();
		} else if (conf.isTest()) {
			LinkedList<ZuluValue> argsc = new LinkedList<>();
			for (String arg : conf.getFiles()) {
				argsc.add(new ZuluString(arg));
			}
			Table.set("Args", new ZuluList(argsc));
			Handler.tests();
		} else if (!conf.getEntry().isBlank()) {
			FileUtils.expectExtention(conf.getEntry(), "zulu");
			Handler.entry(conf.getEntry(), conf.getEntry_module());
		} else if (conf.hasFiles()) {
			for (String file : conf.getFiles()) {
				String ext = FileUtils.getExtension(file);
				if (ext.equals("app")) {
					Modules.get("dist").get("app").execute(new ZuluString(file));
				} else {
					try {
						FileUtils.expectExtention(file, "zulu");
						Handler.handle(SourceLoader.readSource(file), false);
					} catch (IOException e) {
						System.err.println(e.getMessage());
						System.exit(1);
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
		throw new ZuluException(type, sb.toString());
	}
}
