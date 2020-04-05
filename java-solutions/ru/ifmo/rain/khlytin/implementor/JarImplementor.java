package ru.ifmo.rain.khlytin.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;
import info.kgeorgiy.java.advanced.implementor.Impler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Class implementing {@link JarImpler}.
 *
 * @author khlyting
 */
public class JarImplementor extends Implementor implements JarImpler {

    /**
     * Creates a <code>.jar</code> file containing compiled sources of class
     * implemented by {@link #implement(Class, Path)} class in location specified by {@code jarFile}.
     *
     * @param token   type token to create implementation for
     * @param jarFile target <code>.jar</code> file
     * @throws ImplerException if any error occurs during the implementation
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path tmpDir = Paths.get(".");
        implement(token, tmpDir);
        String className = Paths.get(getClassPackage(token).replace('.', File.separatorChar))
                .resolve(getClassSimpleName(token)).toString();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try {
            if (compiler.run(null, null, null, "-classpath",
                    tmpDir.getFileName() + File.pathSeparator +
                            Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                    tmpDir.resolve(className).toString() + ".java") != 0) {
                throw new ImplerException("Failed to compile files");
            }
        } catch (URISyntaxException e) {
            throw new ImplerException("URISyntaxException", e);
        }

        try (JarOutputStream writer = new JarOutputStream(Files.newOutputStream(jarFile))) {
            writer.putNextEntry(new ZipEntry(className.replace(File.separatorChar, '/') + ".class"));
            Files.copy(Paths.get(tmpDir.resolve(className).toString() + ".class"), writer);
        } catch (IOException e) {
            throw new ImplerException("Failed to write to jar file", e);
        }
    }

    /**
     * Main function. Provides console interface for {@link Implementor}.
     * Runs in two modes depending on {@code args}:
     * <ol>
     * <li>2-argument <code>className outputPath</code> creates <code>.java</code> file by executing
     * provided with {@link Impler} method {@link #implement(Class, Path)}</li>
     * <li>3-argument <code>-jar className jarOutputPath</code> creates <code>.jar</code> file by executing
     * provided with {@link JarImpler} method {@link #implementJar(Class, Path)}</li>
     * </ol>
     * All arguments must be correct and not-null. If some arguments are incorrect
     * or an error occurs in runtime an information message is printed and implementation is aborted.
     *
     * @param args command line arguments for application
     */
    public static void main(String[] args) {
        try {
            if (args == null || (args.length != 3 && args.length != 4)) {
                throw new IllegalArgumentException("Two or three arguments were expected");
            }
            for (String arg : args) {
                if (arg == null) {
                    throw new IllegalArgumentException("Non-null arguments were expected");
                }
            }
            JarImplementor implementor = new JarImplementor();

            if (args.length == 3) {
                implementor.implement(Class.forName(args[1]), Paths.get(args[2]));
            } else if (args[1].equals("-jar")) {
                implementor.implementJar(Class.forName(args[2]), Paths.get(args[3]));
            } else {
                throw new IllegalArgumentException(args[1] + " is unknown argument, -jar expected");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal arguments: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class in the first argument: " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("An error occurred during implementation: " + e.getMessage());
        }
    }
}