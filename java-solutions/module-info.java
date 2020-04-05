/**
 * Implementation of {@link info.kgeorgiy.java.advanced.implementor.Impler}
 * and {@link info.kgeorgiy.java.advanced.implementor.JarImpler} interfaces.
 *
 * @author khlyting
 */

module ru.ifmo.rain.khlytin.implementor {
    requires transitive info.kgeorgiy.java.advanced.implementor;
    requires java.compiler;
    requires info.kgeorgiy.java.advanced.concurrent;

    exports ru.ifmo.rain.khlytin.implementor;
}