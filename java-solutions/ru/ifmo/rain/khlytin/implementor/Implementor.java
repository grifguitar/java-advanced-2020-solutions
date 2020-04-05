package ru.ifmo.rain.khlytin.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Class implementing {@link Impler}. Provides public methods to implement <code>.java</code>
 * files for classes extending or implementing given class of interface.
 *
 * @author khlyting
 */
public class Implementor implements Impler {

    /**
     * Space-type indentation for generated <code>.java</code> files.
     */
    private final static String SPACE = " ";

    /**
     * Line separator for generated <code>.java</code> files.
     */
    private final static String EOLN = System.lineSeparator();

    /**
     * End of line character for generated <code>.java</code> files.
     */
    private final static String SEMICOLON = ";";

    /**
     * Tabulation-type indentation for generated <code>.java</code> files.
     */
    private final static String TAB = "    ";

    /**
     * Double Tabulation-type indentation for generated <code>.java</code> files.
     */
    private final static String DOUBLE_TAB = "        ";

    /**
     * Hash providing method wrapper static class. Provides custom equality check for {@link Method}s
     * independent from {@link Method#getDeclaringClass()}.
     *
     * @see Method#hashCode()
     * @see Method#equals(Object)
     */
    private static class HashShell {

        /**
         * Inner wrapped {@link Method} instance.
         */
        private final Method method;

        /**
         * Prime multiplier used in hashing.
         */
        private final static int BASE = 37;

        /**
         * Prime module used in hashing.
         */
        private final static int MOD = (int) (1e9 + 7);

        /**
         * Wrapping constructor. Creates new instance of {@link HashShell} with wrapped {@link Method} inside.
         *
         * @param method instance of {@link Method} class to be wrapped inside
         */
        HashShell(Method method) {
            this.method = method;
        }

        /**
         * Checks if this wrapper is equal to another object.
         * Object is considered equal if it is an instance of {@link HashShell}
         * and has a wrapped {@link #method} inside with same name, parameter types and return type.
         *
         * @param obj object to compare with
         * @return <code>true</code> if objects are equal, <code>false</code> otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HashShell) {
                HashShell other = (HashShell) obj;
                return Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes())
                        && method.getReturnType().equals(other.method.getReturnType())
                        && method.getName().equals(other.method.getName());
            }
            return false;
        }

        /**
         * Hash code calculator. Calculates polynomial hash code for wrapped {@link #method}
         * using it's name, parameter types and return type.
         *
         * @return integer hash code value
         */
        @Override
        public int hashCode() {
            return (method.getName().hashCode()
                    + method.getReturnType().hashCode()) * BASE % MOD
                    + Arrays.hashCode(method.getParameterTypes()) * BASE * BASE % MOD;
        }

        /**
         * Getter for wrapped instance of {@link Method} class.
         *
         * @return wrapped {@link #method}
         */
        public Method getMethod() {
            return method;
        }
    }

    /**
     * Simple name of generated class.
     * @param token given {@link Class} to implement from
     * @return a {@link String} representing simple name of generated class
     */
    protected static String getClassSimpleName(Class<?> token) {
        return token.getSimpleName() + "Impl";
    }

    /**
     * Package of generated class.
     * @param token given {@link Class} to implement from
     * @return a {@link String} representing package of generated class
     */
    protected static String getClassPackage(Class<?> token) {
        return token.getPackage().getName();
    }

    /**
     * Unicode encoder for resulting <code>.java</code> file.
     * Escapes all unicode characters in <code>\\u</code> notation.
     *
     * @param arg a {@link String} instance to be encoded
     * @return a {@link String} representing unicode-escaped {@code arg}
     */
    private static String toUnicode(String arg) {
        StringBuilder builder = new StringBuilder();
        for (char c : arg.toCharArray()) {
            builder.append(c < 128 ? String.valueOf(c) : String.format("\\u%04x", (int) c));
        }
        return builder.toString();
    }

    /**
     * Writes class head implementations of a given {@link Class}
     * through the specified {@code BufferedWriter}.
     *
     * @param token  given {@link Class} to implement
     * @param writer given {@link BufferedWriter}
     * @throws IOException If an I / O error occurs during recording
     */
    private static void implementClassHead(Class<?> token, BufferedWriter writer) throws IOException {
        StringBuilder res = new StringBuilder();

        if (!getClassPackage(token).equals("")) {
            res.append("package").append(SPACE).append(getClassPackage(token)).append(SEMICOLON).append(EOLN);
        }
        res.append(EOLN);

        res.append("public class").append(SPACE).append(getClassSimpleName(token)).append(SPACE)
                .append((token.isInterface() ? "implements" : "extends")).append(SPACE)
                .append(token.getCanonicalName()).append(SPACE).append("{").append(EOLN);

        writer.write(toUnicode(res.toString()));
    }

    /**
     * Return a string describing modifiers filtered from
     * {@link Modifier#ABSTRACT}, {@link Modifier#NATIVE} and {@link Modifier#TRANSIENT} modifiers.
     *
     * @param mod integer representing modifiers mask
     * @return a {@link String} representation of filtered modifiers
     * @see Method#getModifiers()
     * @see Class#getModifiers()
     * @see Modifier#toString(int)
     */
    private static String getModifiersString(int mod) {
        return Modifier.toString(mod & (Integer.MAX_VALUE
                - Modifier.ABSTRACT
                - Modifier.NATIVE
                - Modifier.TRANSIENT));
    }

    /**
     * Gets the return type of {@link Executable} if it is instance of {@link Method}
     * through the specified {@code BufferedWriter}. Otherwise returns an empty string.
     *
     * @param executable given {@link Executable}
     * @return a {@link String} representation of return type
     */
    private static String getReturnType(Executable executable) {
        if (!(executable instanceof Method)) return "";
        Method method = (Method) executable;
        return method.getReturnType().getCanonicalName();
    }

    /**
     * Returns a representation of {@link Executable} argument list with types and names.
     *
     * @param executable an instance of {@link Executable}
     * @param withTypes  indicates whether to write argument types
     * @return a {@link String} representing this argument list
     */
    private static String getArguments(Executable executable, boolean withTypes) {
        return Arrays.stream(executable.getParameters())
                .map(parameter -> (withTypes ? parameter.getType().getCanonicalName() + SPACE : "")
                        + parameter.getName()).collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * Returns a representation of {@link Executable} exceptions list.
     *
     * @param executable an instance of {@link Executable}
     * @return a {@link String} representing all throws from this {@code executable}
     */
    private static String getExceptions(Executable executable) {
        String res = Arrays.stream(executable.getExceptionTypes())
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
        if (res.length() != 0) return SPACE + "throws" + SPACE + res;
        return "";
    }

    /**
     * Returns default return value representation for method with given {@link Method#getReturnType()}.
     *
     * @param token some method return value type
     * @return a {@link String} representing default return value of this type
     */
    private static String getDefaultValue(Class<?> token) {
        if (!token.isPrimitive()) {
            return " null";
        } else if (token.equals(void.class)) {
            return "";
        } else if (token.equals(boolean.class)) {
            return " false";
        } else {
            return " 0";
        }
    }

    /**
     * Writes fully constructed {@link Executable}
     * through the specified {@code BufferedWriter}.
     * If given executable is {@link Constructor}, written implementation
     * calls constructor of superclass. If given executable is {@link Method},
     * written implementation returns default value of its return type.
     *
     * @param executable     given {@link Executable}
     * @param writer         given {@link BufferedWriter}
     * @param executableName given {@link String}
     * @throws IOException If an I / O error occurs during recording
     */
    private static void implementExecutable(Executable executable, BufferedWriter writer, String executableName)
            throws IOException {
        //No reason to use StringBuilder here, casual String is ok here

        String res = TAB + getModifiersString(executable.getModifiers()) + SPACE +
                getReturnType(executable) + SPACE +
                executableName +
                getArguments(executable, true) +
                getExceptions(executable) + SPACE + "{" + SPACE + EOLN +
                DOUBLE_TAB + ((executable instanceof Method)
                ? "return" + getDefaultValue(((Method) executable).getReturnType())
                : "super" + getArguments(executable, false)) +
                SEMICOLON + EOLN +
                TAB + "}" + EOLN +
                EOLN;
        writer.write(toUnicode(res));
    }

    /**
     * Writes implementations of all non-private constructors of a given {@link Class}
     * through the specified {@code BufferedWriter}.
     *
     * @param token  given {@link Class} to implement constructors from
     * @param writer given {@link BufferedWriter}
     * @throws IOException     If an I / O error occurs during recording
     * @throws ImplerException If given {@link Class} has not non-private constructors
     */
    private static void implementConstructors(Class<?> token, BufferedWriter writer)
            throws IOException, ImplerException {
        boolean flag = false;
        for (Constructor<?> constructor : token.getDeclaredConstructors()) {
            if (Modifier.isPrivate(constructor.getModifiers())) continue;
            flag = true;
            implementExecutable(constructor, writer, getClassSimpleName(token));
        }
        if (!flag) {
            throw new ImplerException("No non-private constructors");
        }
    }

    /**
     * Puts all abstract methods from a given array to a given {@link HashSet}
     * converted into {@link HashShell} wrapper.
     *
     * @param methods array of {@link Method} to get methods from
     * @param hashSet {@link HashSet} of {@link HashShell} to put unique (@code HashShell} in
     */
    private static void putAbstractMethods(Method[] methods, HashSet<HashShell> hashSet) {
        Arrays.stream(methods)
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .map(HashShell::new)
                .collect(Collectors.toCollection(() -> hashSet));
    }

    /**
     * Writes implementations of all abstract methods of a given {@link Class}
     * through the specified {@code BufferedWriter}.
     *
     * @param token  given {@link Class} to implement methods from
     * @param writer given {@link BufferedWriter}
     * @throws IOException If an I / O error occurs during recording
     */
    private static void implementMethods(Class<?> token, BufferedWriter writer) throws IOException {
        HashSet<HashShell> hashShells = new HashSet<>();
        putAbstractMethods(token.getMethods(), hashShells);
        while (token != null) {
            putAbstractMethods(token.getDeclaredMethods(), hashShells);
            token = token.getSuperclass();
        }
        for (HashShell hashShell : hashShells) {
            Method method = hashShell.getMethod();
            implementExecutable(method, writer, method.getName());
        }
    }

    /**
     * Builds a path from the specified parts {@link Path}, {@link String} and returns it.
     * @param path given {@link Path} representing path
     * @param classPackage given {@link String} representing class package
     * @param classSimpleName given {@link String} representing simple class name
     * @return a {@link File} representing path to output file
     * @throws ImplerException if failed to create path to output file
     */
    private static File getOutputFile(Path path, String classPackage, String classSimpleName) throws ImplerException {
        File classFile = new File(Paths.get(path.toString(),
                classPackage.split("\\.")).resolve(classSimpleName).toString() + ".java");

        if (!classFile.exists()) {
            File outputParent = classFile.getParentFile();
            if (outputParent != null && !outputParent.exists() && !outputParent.mkdirs()) {
                throw new ImplerException("Failed to create path to output file");
            }
        }

        return classFile;
    }

    /**
     * Creates a <code>.java</code> file containing source code of a class extending or implementing
     * class or interface specified by {@code token} in location specified by {@code root}.
     *
     * @param token type token to create implementation for
     * @param root root directory
     * @throws ImplerException if any error occurs during the implementation
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token == Enum.class || token.isArray() || token.isPrimitive()
                || Modifier.isPrivate(token.getModifiers()) || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Incorrect class token");
        }

        File classFile = getOutputFile(root, token.getPackage().getName(), getClassSimpleName(token));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(classFile))) {
            implementClassHead(token, writer);

            if (!token.isInterface()) {
                implementConstructors(token, writer);
            }
            implementMethods(token, writer);

            writer.write(toUnicode("}" + EOLN));
        } catch (IOException e) {
            throw new ImplerException("Failed to write to output file", e);
        }
    }
}
