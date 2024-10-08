package com.zulu.runtime;

import com.zulu.utils.ZuluException;
import com.zulu.utils.FileUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 *
 * @author hexaredecimal
 */
public class LibraryLoader {

	private static HashMap<String, URLClassLoader> loaded = new HashMap<>();
	public static HashMap<String, Class<?>> primitives = new HashMap<>();

	public static void init() {
		primitives.put("int", int.class);
		primitives.put("double", double.class);
		primitives.put("float", float.class);
		primitives.put("String", String.class);
	}

	private static URLClassLoader loadLibrary(String name) {
		name += ".jar";
		if (loaded.containsKey(name)) {
			return loaded.get(name);
		}

		HashMap<String, String> files = FileUtils.getFilesWithExtension(".", ".jar");
		if (!files.containsKey(name)) {
			throw new ZuluException("Internal", "Failed to locate a library file (.jar) with name " + name);
		}
		String path = files.get(name);

		File library_ptr = new File(path);
		URLClassLoader loader = null;
		try {
			loader = new URLClassLoader(new URL[]{library_ptr.toURI().toURL()});
			loaded.put(name, loader);
		} catch (MalformedURLException e) {
			throw new ZuluException("Internal", "Malformed path to library file (.jar) with name " + name);
		}
		return loader;
	}

	public static Class<?> loadClass(String library, String class_name) {
		Class<?> clz = null;
		try {
			clz = Class.forName(class_name);
		} catch (ClassNotFoundException ex) {
			URLClassLoader loader = loadLibrary(library);
			try {
				clz = loader.loadClass(class_name);
			} catch (ClassNotFoundException e) {
				throw new ZuluException("Internal", "Class `" + class_name + "` not found in library " + library);
			}
		}
		return clz;
	}

}
