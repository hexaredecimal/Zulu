package com.erlava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author hexaredecimal
 */
public class Config {

	private ArrayList<String> files;
	private String version;
	private String author;
	private boolean repl;
	private String name;
	private boolean test;
	private String entry = "";
	private String entry_module = "";

	public Config(String[] files, String v, String auth, boolean r) {
		this.files = new ArrayList<>();
		for (String file : files) {
			this.files.add(file);
		}
		this.version = v;
		this.author = auth;
		this.repl = r;
		this.name = "";
		this.test = false;
		this.entry = "main.lava";
		this.entry_module = "main";
	}

	public Config() {
		this.files = new ArrayList<>();
	}

	public String getEntry() {
		if (Config.isProject()) {
			return "code/main.lava";
		}
		return entry;
	}

	public String getEntry_module() {
		if (Config.isProject()) {
			return "main";
		}
		return entry_module;
	}

	public boolean isTest() {
		return this.test;
	}

	public String getVersion() {
		return version;
	}

	public String getAuthor() {
		return author;
	}

	public void setProgram(String name) {
		this.name = name;
	}

	public String[] getFiles() {
		if (Config.isProject()) {
			this.files.add("code/main.lava");
		}

		Object[] objs = this.files.toArray();
		String[] files = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			String file = (String) objs[i];
			files[i] = file;
		}
		return files;
	}

	public boolean isRepl() {
		return repl;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setRepl(boolean repl) {
		this.repl = repl;
	}

	public void addFile(String file) {
		this.files.add(file);
	}

	public void help() {
		String out_put = this.name.concat(" [options] ");
		out_put += "\n\n";
		out_put
			+= "[options]\n"
				.concat("\tentry <filepath>\t - Selects which file to start execution execution from.\n")
				.concat("\tnew\t\t\t - Creates a new project\n")
				.concat("\trepl \t\t\t - Starts the repl\n")
				.concat("\ttest\t\t\t - Run the compiler in test mode\n")
				.concat("\thelp\t\t\t - Displays this help information\n")
				.concat("\tversion\t\t\t - Displays the version of this program\n");

		String logo
			= "\t___________      .__                 \n"
			+ "\t\\_   ____________|  | _____ ___  ______\n"
			+ "\t |    __)_\\_  __ |  | \\__  \\  \\/ \\__  \\ \n"
			+ "\t |        \\|  | \\|  |__/ __ \\   / / __ \\_ \n"
			+ "\t/_______  /|__|  |____(____  /\\_/ (____  / \n"
			+ "\t        \\/                 \\/          \\/ \n"
			+ "\t\t a proud fork of Barley :) \n\n"
			+ "\t\t (c) 2024 - Gama Sibusiso\n\n";
		System.out.println(out_put);
		System.out.println(logo);
		System.exit(101);
	}

	public void showVersion() {
		System.out.println(this.name.concat(" version ").concat(this.version));
		System.exit(0);
	}

	public void reportError(String err) {
		System.out.println("error: ".concat(err));
		System.exit(101);
	}

	public void initNewProject(String project_name) {
		if (project_name != null) {
			createProjectFile(name);
			return;
		}

		try (Scanner sc = new Scanner(System.in);) {
			System.out.print("Enter project name: ");
			String name = sc.nextLine();

			if (name.isBlank()) {
				System.err.println("Error: Invalid project name");
				System.exit(1);
			}

			createProjectFile(name);
			System.out.println("Successfully created a new project");
			System.exit(0);
			return;
		} catch (Exception ex) {

		}
	}

	public static boolean isProject() {
		File project = new File("project.toml");
		File project_dir = new File(".pkg");
		return project.exists() && project_dir.exists() && project_dir.isDirectory();
	}

	private void createProjectFile(String project_name) {
		if (isProject()) {
			System.err.println("Error: project files already exists");
			System.exit(1);
		}

		String code
			= """
-module(main).
-opt().
-doc("Hello, world").

-import(io.[writeln]).

main() -> writeln("Hello, world").
""";
		StringBuilder sb = new StringBuilder();
		sb
			.append("[package]".indent(0))
			.append(String.format("name = \"%s\"", project_name).indent(0))
			.append(String.format("version = \"%s\"", "0.0.1").indent(0))
			.append("authors = []".indent(0))
			.append("\n".indent(0))
			.append("[dependencies]".indent(0));

		String project_file = "project.toml";
		File fp = new File(project_file);
		File fp_dir = new File(".pkg");
		File src_dir = new File("code");
		try {
			File main_file = new File("code/main.lava");
			fp_dir.mkdirs();
			src_dir.mkdirs();
			fp.createNewFile();
			main_file.createNewFile();

			try (FileWriter fw = new FileWriter(main_file)) {
				fw.write(code);
			}
			try (FileWriter fw = new FileWriter(fp)) {
				fw.write(sb.toString());
			}
			FileAttributes att = new FileAttributes(fp.getAbsolutePath());
			att.saveAttributes();
		} catch (IOException e) {
			System.err.println("Error: failed to initialize a new project: " + e.getMessage());
			System.exit(1);
		}
	}

	public void parse(ArgParser parser) {
		String top = parser.get();
		boolean is_adding_files = false;
		boolean has_error = false;
		while (top != null) {

			if (top.equals("entry")) {
				//entry main.elv
				String filepath = parser.get();
				if (filepath == null) {
					has_error = true;
					break;
				}
				int dotIndex = filepath.indexOf(".");
				this.entry_module = filepath.substring(0, dotIndex);
				this.entry = filepath;
				break;
			} else if (top.equals("test")) {
				test = true;
				if (is_adding_files) {
					has_error = true;
					break;
				}
			} else if (top.equals("new")) {
				String name = parser.get();
				this.initNewProject(name);
				break;
			} else if (top.equals("repl")) {
				if (is_adding_files) {
					has_error = true;
					break;
				}
				this.setRepl(true);
			} else if (top.equals("help")) {
				if (is_adding_files) {
					has_error = true;
					break;
				}
				this.help();
			} else if (top.equals("version")) {
				if (is_adding_files) {
					has_error = true;
					break;
				}
				this.showVersion();
			} else {
				is_adding_files = true;
				this.addFile(top);
			}
			top = parser.get();
		}

		if (has_error) {
			reportError("Attempt to set `".concat("argument").concat("` while reading files"));
		}
	}

	boolean hasFiles() {
		return this.files.size() > 0;
	}
}
