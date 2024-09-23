package com.zulu.reflection;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.Table;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.Modules;
import com.zulu.runtime.ZuluFunction;
import com.zulu.runtime.ZuluReference;
import com.zulu.memory.Storage;
import com.zulu.utils.Arguments;
import com.zulu.utils.BarleyException;
import com.zulu.utils.CallStack;
import com.zulu.utils.Function;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import com.zulu.runtime.ZuluValue;

public class Reflection {

	private static final ZuluValue NULL = new NullValue();

	public static void initConstants() {
	}

	public void inject() {
		initConstants();
		Table.define("null", NULL);
		Table.define("boolean.class", new ClassValue(boolean.class));
		Table.define("boolean[].class", new ClassValue(boolean[].class));
		Table.define("boolean[][].class", new ClassValue(boolean[][].class));
		Table.define("byte.class", new ClassValue(byte.class));
		Table.define("byte[].class", new ClassValue(byte[].class));
		Table.define("byte[][].class", new ClassValue(byte[][].class));
		Table.define("short.class", new ClassValue(short.class));
		Table.define("short[].class", new ClassValue(short[].class));
		Table.define("short[][].class", new ClassValue(short[][].class));
		Table.define("char.class", new ClassValue(char.class));
		Table.define("char[].class", new ClassValue(char[].class));
		Table.define("char[][].class", new ClassValue(char[][].class));
		Table.define("int.class", new ClassValue(int.class));
		Table.define("int[].class", new ClassValue(int[].class));
		Table.define("int[][].class", new ClassValue(int[][].class));
		Table.define("long.class", new ClassValue(long.class));
		Table.define("long[].class", new ClassValue(long[].class));
		Table.define("long[][].class", new ClassValue(long[][].class));
		Table.define("float.class", new ClassValue(float.class));
		Table.define("float[].class", new ClassValue(float[].class));
		Table.define("float[][].class", new ClassValue(float[][].class));
		Table.define("double.class", new ClassValue(double.class));
		Table.define("double[].class", new ClassValue(double[].class));
		Table.define("double[][].class", new ClassValue(double[][].class));
		Table.define("String.class", new ClassValue(String.class));
		Table.define("String[].class", new ClassValue(String[].class));
		Table.define("String[][].class", new ClassValue(String[][].class));
		Table.define("Object.class", new ClassValue(Object.class));
		Table.define("Object[].class", new ClassValue(Object[].class));
		Table.define("Object[][].class", new ClassValue(Object[][].class));

		HashMap<String, Function> reflection = new HashMap<>();
		reflection.put("is_null", this::isNull);
		reflection.put("class", this::newClass);
		reflection.put("toObject", this::toObject);
		reflection.put("toValue", this::toValue);
		reflection.put("extract", args -> {
			Arguments.check(2, args.length);
			ClassValue cl = ((ClassValue) args[0]);
			return cl.injection.get(args[1].toString());
		});
		reflection.put("object", args -> {
			Arguments.check(2, args.length);
			ObjectValue cl = ((ObjectValue) args[0]);
			return cl.injection.get(args[1].toString());
		});
		reflection.put("instance", args -> {
			Arguments.checkAtLeast(1, args.length);
			ClassValue cl = ((ClassValue) args[0]);
			return cl.newInstance(List.of(args).subList(1, args.length).toArray(new ZuluValue[]{}));
		});
		reflection.put("call", args -> {
			Arguments.checkAtLeast(2, args.length);
			ObjectValue cl = ((ObjectValue) args[0]);
			//return ((Function) cl.injection.get(args[1].toString())).execute(List.of(args).subList(2, args.length).toArray(new BarleyValue[]{}));
			//return ((BarleyFunction) getValue(cl.object.getClass(), cl.object, args[1].toString())).execute(List.of(args).subList(2, args.length).toArray(new BarleyValue[]{}));
			return ((ZuluFunction) getValue(cl.object.getClass(), cl.object, args[1].toString())).execute(List.of(args).subList(2, args.length).toArray(new ZuluValue[]{}));
		});
		reflection.put("object_to_string", args -> {
			Arguments.check(1, args.length);
			ObjectValue cl = ((ObjectValue) args[0]);
			return new ZuluString(cl.object.toString());
		});
		reflection.put("static", args -> {
			Arguments.check(2, args.length);
			ClassValue val = ((ClassValue) args[0]);
			return val.injection.get(args[1].toString());
		});
		reflection.put("null", args -> new NullValue());

		Modules.put("reflection", reflection);
	}

	//<editor-fold defaultstate="collapsed" desc="Values">
	private static class NullValue implements ZuluValue {

		@Override
		public BigInteger asInteger() {
			return BigInteger.ONE;
		}

		@Override
		public BigDecimal asFloat() {
			return BigDecimal.ONE;
		}

		@Override
		public Object raw() {
			return null;
		}

		@Override
		public String toString() {
			return "null";
		}
	}

	private static class ClassValue implements ZuluValue {

		public HashMap<String, ZuluValue> injection;

		public static ZuluValue classOrNull(Class<?> clazz) {
			if (clazz == null) {
				return NULL;
			}
			return new ClassValue(clazz);
		}

		private final Class<?> clazz;

		public ClassValue(Class<?> clazz) {
			this.clazz = clazz;
			this.injection = new HashMap<>();
			init(clazz);
		}

		public ZuluValue set(String key, ZuluValue value) {
			return injection.put(key, value);
		}

		private void init(Class<?> clazz) {
			set("isAnnotation", ZuluNumber.fromBoolean(clazz.isAnnotation()));
			set("isAnonymousClass", ZuluNumber.fromBoolean(clazz.isAnonymousClass()));
			set("isArray", ZuluNumber.fromBoolean(clazz.isArray()));
			set("isEnum", ZuluNumber.fromBoolean(clazz.isEnum()));
			set("isInterface", ZuluNumber.fromBoolean(clazz.isInterface()));
			set("isLocalClass", ZuluNumber.fromBoolean(clazz.isLocalClass()));
			set("isMemberClass", ZuluNumber.fromBoolean(clazz.isMemberClass()));
			set("isPrimitive", ZuluNumber.fromBoolean(clazz.isPrimitive()));
			set("isSynthetic", ZuluNumber.fromBoolean(clazz.isSynthetic()));

			set("modifiers", new ZuluNumber(clazz.getModifiers()));

			set("canonicalName", new ZuluString(clazz.getCanonicalName()));
			set("name", new ZuluString(clazz.getName()));
			set("simpleName", new ZuluString(clazz.getSimpleName()));
			set("typeName", new ZuluString(clazz.getTypeName()));
			set("genericString", new ZuluString(clazz.toGenericString()));

			set("getComponentType", new ZuluFunction(v -> classOrNull(clazz.getComponentType())));
			set("getDeclaringClass", new ZuluFunction(v -> classOrNull(clazz.getDeclaringClass())));
			set("getEnclosingClass", new ZuluFunction(v -> classOrNull(clazz.getEnclosingClass())));
			set("getSuperclass", new ZuluFunction(v -> new ClassValue(clazz.getSuperclass())));

			set("getClasses", new ZuluFunction(v -> array(clazz.getClasses())));
			set("getDeclaredClasses", new ZuluFunction(v -> array(clazz.getDeclaredClasses())));
			set("getInterfaces", new ZuluFunction(v -> array(clazz.getInterfaces())));

			set("asSubclass", new ZuluFunction(this::asSubclass));
			set("isAssignableFrom", new ZuluFunction(this::isAssignableFrom));
			set("new", new ZuluFunction(this::newInstance));
			set("cast", new ZuluFunction(this::cast));

			for (Method method : clazz.getDeclaredMethods()) {
				try {
					method.setAccessible(true);
				} catch (InaccessibleObjectException ignored) {

				}
				set(method.getName(), new ZuluFunction(args -> {
					try {
						Object[] vals = valuesToObjects(args);
						return objectToValue(method.invoke(clazz, vals));
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					return new ZuluAtom("error");
				}));
			}
		}

		private ZuluValue asSubclass(ZuluValue[] args) {
			Arguments.check(1, args.length);
			return new ClassValue(clazz.asSubclass(((ClassValue) args[0]).clazz));
		}

		private ZuluValue isAssignableFrom(ZuluValue[] args) {
			Arguments.check(1, args.length);
			return ZuluNumber.fromBoolean(clazz.isAssignableFrom(((ClassValue) args[0]).clazz));
		}

		public ZuluValue newInstance(ZuluValue[] args) {
			return findConstructorAndInstantiate(args, clazz.getDeclaredConstructors());
		}

		private ZuluValue cast(ZuluValue[] args) {
			Arguments.check(1, args.length);
			return objectToValue(clazz, clazz.cast(((ObjectValue) args[0]).object));
		}

		@Override
		public String toString() {
			return "#ClassValue" + injection;
		}

		@Override
		public BigInteger asInteger() {
			return BigInteger.ONE;
		}

		@Override
		public BigDecimal asFloat() {
			return BigDecimal.ONE;
		}

		@Override
		public Object raw() {
			return injection;
		}
	}

	public static class ObjectValue implements ZuluValue {

		public HashMap<String, ZuluValue> injection;

		public static ZuluValue objectOrNull(Object object) {
			if (object == null) {
				return NULL;
			}
			return new ObjectValue(object);
		}

		public ZuluValue set(String key, ZuluValue value) {
			return injection.put(key, value);
		}

		public final Object object;

		static {
		}

		public ObjectValue(Object object) {
			this.injection = new HashMap<>();
			this.object = object;
			Storage.segment(this);
		}

		public ZuluValue get(ZuluValue key) {
			return getValue(object.getClass(), object, key.toString());
		}

		@Override
		public String toString() {
			return "#ObjectValue" + injection;
		}

		@Override
		public BigInteger asInteger() {
			return BigInteger.ONE;
		}

		@Override
		public BigDecimal asFloat() {
			return BigDecimal.ONE;
		}

		@Override
		public Object raw() {
			return object;
		}
	}
//</editor-fold>

	private ZuluValue isNull(ZuluValue[] args) {
		Arguments.checkAtLeast(1, args.length);
		for (ZuluValue arg : args) {
			if (arg.toString().equals("null")) {
				return new ZuluNumber(1);
			}
		}
		return new ZuluNumber(1);
	}

	private ZuluValue newClass(ZuluValue[] args) {
		Arguments.check(1, args.length);

		final String className = args[0].toString();
		try {
			return new ClassValue(Class.forName(className));
		} catch (ClassNotFoundException ce) {
			throw new RuntimeException("Class " + className + " not found.", ce);
		}
	}

	private ZuluValue toObject(ZuluValue[] args) {
		Arguments.check(1, args.length);
		if (args[0] == NULL) {
			return NULL;
		}
		return new ObjectValue(Objects.requireNonNull(valueToObject(args[0])));
	}

	private ZuluValue toValue(ZuluValue[] args) {
		Arguments.check(1, args.length);
		if (args[0] instanceof ObjectValue) {
			return objectToValue(((ObjectValue) args[0]).object);
		}
		return NULL;
	}

	//<editor-fold defaultstate="collapsed" desc="Helpers">
	private static ZuluValue getValue(Class<?> clazz, Object object, String key) {
		// Trying to get field
		try {
			final Field field = clazz.getField(key);
			return new ZuluFunction(b -> {
				try {
					field.setAccessible(true);
					return objectToValue(field.getType(), field.get(object));
				} catch (IllegalAccessException | InaccessibleObjectException e) {
					return new ZuluAtom("reflection_error");
				}
			});
		} catch (NoSuchFieldException | SecurityException
			| IllegalArgumentException ex) {
			// ignore and go to the next step
		}

		// Trying to invoke method
		try {
			final Method[] allMethods = clazz.getMethods();
			final List<Method> methods = new ArrayList<>();
			for (Method method : allMethods) {
				if (method.getName().equals(key)) {
					methods.add(method);
				}
			}
			if (methods.isEmpty()) {
				return new ZuluFunction(a -> new ZuluAtom("empty"));
			}
			return new ZuluFunction(methodsToFunction(object, methods));
		} catch (SecurityException ex) {
			// ignore and go to the next step
		}

		return NULL;
	}

	private static ZuluValue findConstructorAndInstantiate(ZuluValue[] args, Constructor<?>[] ctors) {
		for (Constructor<?> ctor : ctors) {
			ctor.setAccessible(true);
			if (ctor.getParameterCount() != args.length) {
				continue;
			}
			if (!isMatch(args, ctor.getParameterTypes())) {
				continue;
			}
			try {
				final Object result = ctor.newInstance(valuesToObjects(args));
				return new ObjectValue(result);
			} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InaccessibleObjectException | InvocationTargetException ex) {
				// skip
			}
		}

		Object[] as = valuesToObjects(args);
		Class<?>[] types = new Class[as.length];
		for (int i = 0; i < as.length; i++) {
			types[i] = as[i].getClass();
		}

		throw new BarleyException("BadReflection", "Can't find constructor for length " + args.length + " and for constructors " + Arrays.toString(ctors) + ". when args: " + Arrays.toString(args) + "\n    when types of args is: " + Arrays.toString(types));
	}

	private static Function methodsToFunction(Object object, List<Method> methods) {
		return (args) -> {
			for (Method method : methods) {
				try {
					method.setAccessible(true);
				} catch (IllegalArgumentException ex) {
					// skip
				} catch (InaccessibleObjectException ex) {

				}
				if (method.getParameterCount() != args.length) {
					continue;
				}
				if (!isMatch(args, method.getParameterTypes())) {
					continue;
				}
				try {
					try {
						method.setAccessible(true);
					} catch (InaccessibleObjectException ex) {

					}
					final Object result = method.invoke(object, valuesToObjects(args));
					if (method.getReturnType() != void.class) {
						return objectToValue(result);
					}
					return new ZuluNumber(1);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | BarleyException ex) {
					System.err.println(ex.getCause());
					if (ex instanceof BarleyException x) {
						System.out.printf("**  reflection error: %s\n", x.getText());
						int count = CallStack.getCalls().size();
						if (count == 0) {
							System.exit(1);
						}
						System.out.println(String.format("\nCall stack was:"));
						for (CallStack.CallInfo info : CallStack.getCalls()) {
							System.out.println("    " + count + ". " + info);
							count--;
						}
						System.exit(1);
					}
				}
			}
			final String className = (object == null ? "null" : object.getClass().getName());
			System.out.println(Arrays.toString(object.getClass().getDeclaredMethods()));
			throw new BarleyException("BadReflection", "Method" + "  with " + args.length + " arguments"
				+ " not found or non accessible in " + className);
		};
	}

	private static boolean isMatch(ZuluValue[] args, Class<?>[] types) {
		for (int i = 0; i < args.length; i++) {
			final ZuluValue arg = args[i];
			final Class<?> clazz = types[i];

			if (arg == NULL) {
				continue;
			}

			final Class<?> unboxed = unboxed(clazz);
			boolean assignable = unboxed != null;
			final Object object = valueToObject(arg);
			assignable = assignable && (object != null);
			if (assignable && unboxed.isArray() && object.getClass().isArray()) {
				final Class<?> uComponentType = unboxed.getComponentType();
				final Class<?> oComponentType = object.getClass().getComponentType();
				assignable = assignable && (uComponentType != null);
				assignable = assignable && (oComponentType != null);
				assignable = assignable && (uComponentType.isAssignableFrom(oComponentType));
			} else {
				assignable = assignable && (unboxed.isAssignableFrom(object.getClass()));
			}
			if (assignable) {
				continue;
			}

			return false;
		}
		return true;
	}

	private static Class<?> unboxed(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		if (clazz.isPrimitive()) {
			if (int.class == clazz) {
				return Integer.class;
			}
			if (boolean.class == clazz) {
				return Boolean.class;
			}
			if (double.class == clazz) {
				return Double.class;
			}
			if (float.class == clazz) {
				return Float.class;
			}
			if (long.class == clazz) {
				return Long.class;
			}
			if (byte.class == clazz) {
				return Byte.class;
			}
			if (char.class == clazz) {
				return Character.class;
			}
			if (short.class == clazz) {
				return Short.class;
			}
			if (void.class == clazz) {
				return Void.class;
			}
		}
		return clazz;
	}

	private static ZuluList array(Class<?>[] classes) {
		final ZuluList result = new ZuluList(classes.length);
		for (int i = 0; i < classes.length; i++) {
			result.set(i, ClassValue.classOrNull(classes[i]));
		}
		return result;
	}

	private static ZuluValue objectToValue(Object o) {
		if (o == null) {
			return NULL;
		}
		return objectToValue(o.getClass(), o);
	}

	private static ZuluValue objectToValue(Class<?> clazz, Object o) {
		if (o == null || o == NULL) {
			return NULL;
		}
		if (clazz.isPrimitive()) {
			if (int.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((int) o);
			}
			if (boolean.class.isAssignableFrom(clazz)) {
				return ZuluNumber.fromBoolean((boolean) o);
			}
			if (double.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((double) o);
			}
			if (float.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((float) o);
			}
			if (long.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((long) o);
			}
			if (byte.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((byte) o);
			}
			if (char.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((char) o);
			}
			if (short.class.isAssignableFrom(clazz)) {
				return ZuluNumber.of((short) o);
			}
		}
		if (Number.class.isAssignableFrom(clazz)) {
			return ZuluNumber.of((Number) o);
		}
		if (String.class.isAssignableFrom(clazz)) {
			return new ZuluString((String) o);
		}
		if (CharSequence.class.isAssignableFrom(clazz)) {
			return new ZuluString(((CharSequence) o).toString());
		}
		if (Boolean.class.isAssignableFrom(clazz)) {
			return new ZuluAtom(o.toString());
		}
		if (o instanceof ZuluNumber) {
			return (ZuluValue) o;
		}
		if (clazz.isArray()) {
			return arrayToValue(clazz, o);
		}

		final Class<?> componentType = clazz.getComponentType();
		if (componentType != null) {
			return objectToValue(componentType, o);
		}
		return new ObjectValue(o);
	}

	private static ZuluValue arrayToValue(Class<?> clazz, Object o) {
		final int length = Array.getLength(o);
		final ZuluList result = new ZuluList(length);
		final Class<?> componentType = clazz.getComponentType();
		int i = 0;
		if (boolean.class.isAssignableFrom(componentType)) {
			for (boolean element : (boolean[]) o) {
				result.set(i++, ZuluNumber.fromBoolean(element));
			}
		} else if (byte.class.isAssignableFrom(componentType)) {
			for (byte element : (byte[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (char.class.isAssignableFrom(componentType)) {
			for (char element : (char[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (double.class.isAssignableFrom(componentType)) {
			for (double element : (double[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (float.class.isAssignableFrom(componentType)) {
			for (float element : (float[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (int.class.isAssignableFrom(componentType)) {
			for (int element : (int[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (long.class.isAssignableFrom(componentType)) {
			for (long element : (long[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else if (short.class.isAssignableFrom(componentType)) {
			for (short element : (short[]) o) {
				result.set(i++, ZuluNumber.of(element));
			}
		} else {
			for (Object element : (Object[]) o) {
				result.set(i++, objectToValue(element));
			}
		}
		return result;
	}

	private static Object[] valuesToObjects(ZuluValue[] args) {
		Object[] result = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			result[i] = valueToObject(args[i]);
		}
		return result;
	}

	private static Object valueToObject(ZuluValue value) {
		if (value == NULL) {
			return null;
		}
		if (value instanceof ZuluNumber n) {
			return n.raw();
		} else if (value instanceof ZuluString s) {
			return s.toString();
		} else if (value instanceof ZuluList l) {
			return arrayToObject(l);
		} else if (value instanceof ZuluReference r) {
			return r.getRef();
		} else if (value instanceof ZuluAtom a) {
			return a.toString();
		}
		if (value instanceof ObjectValue) {
			return ((ObjectValue) value).object;
		}
		if (value instanceof ClassValue) {
			return ((ClassValue) value).clazz;
		}
		return value.raw();
	}

	private static Object arrayToObject(ZuluList value) {
		final int size = value.getList().size();
		final Object[] array = new Object[size];
		if (size == 0) {
			return array;
		}

		Class<?> elementsType = null;
		for (int i = 0; i < size; i++) {
			array[i] = valueToObject(value.getList().get(i));
			if (i == 0) {
				elementsType = array[0].getClass();
			} else {
				elementsType = mostCommonType(elementsType, array[i].getClass());
			}
		}

		if (elementsType.equals(Object[].class)) {
			return array;
		}
		return typedArray(array, size, elementsType);
	}

	private static <T, U> T[] typedArray(U[] elements, int newLength, Class<?> elementsType) {
		@SuppressWarnings("unchecked")
		T[] copy = (T[]) Array.newInstance(elementsType, newLength);
		System.arraycopy(elements, 0, copy, 0, Math.min(elements.length, newLength));
		return copy;
	}

	private static Class<?> mostCommonType(Class<?> c1, Class<?> c2) {
		if (c1.equals(c2)) {
			return c1;
		} else if (c1.isAssignableFrom(c2)) {
			return c1;
		} else if (c2.isAssignableFrom(c1)) {
			return c2;
		}
		final Class<?> s1 = c1.getSuperclass();
		final Class<?> s2 = c2.getSuperclass();
		if (s1 == null && s2 == null) {
			final List<Class<?>> upperTypes = Arrays.asList(
				Object.class, void.class, boolean.class, char.class,
				byte.class, short.class, int.class, long.class,
				float.class, double.class);
			for (Class<?> type : upperTypes) {
				if (c1.equals(type) && c2.equals(type)) {
					return s1;
				}
			}
			return Object.class;
		} else if (s1 == null || s2 == null) {
			if (c1.equals(c2)) {
				return c1;
			}
			if (c1.isInterface() && c1.isAssignableFrom(c2)) {
				return c1;
			}
			if (c2.isInterface() && c2.isAssignableFrom(c1)) {
				return c2;
			}
		}

		if (s1 != null) {
			return mostCommonType(s1, c2);
		} else {
			return mostCommonType(c1, s2);
		}
	}
}
