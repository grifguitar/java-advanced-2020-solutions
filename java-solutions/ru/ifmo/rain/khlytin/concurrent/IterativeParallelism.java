package ru.ifmo.rain.khlytin.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class implementing {@link ListIP}.
 *
 * @author khlyting
 */
public class IterativeParallelism implements ListIP {

    private <T, M, R> R commonTask(int threads, List<? extends T> values,
                                   Function<Stream<? extends T>, M> process,
                                   Function<Stream<? extends M>, R> reduce) throws InterruptedException {

        List<Stream<? extends T>> parts = new ArrayList<>();
        int block = values.size() / threads;
        int remainder = values.size() % threads;

        int position = 0;
        for (int i = 0; i < threads; i++) {
            int currentBlock = block + (i < remainder ? 1 : 0);
            if (currentBlock > 0) {
                parts.add(values.subList(position, position + currentBlock).stream());
            }
            position += currentBlock;
        }

        List<M> counted;

        counted = new ArrayList<>(Collections.nCopies(parts.size(), null));
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            final int index = i;
            Thread thread = new Thread(() -> counted.set(index, process.apply(parts.get(index))));
            workers.add(thread);
            thread.start();
        }

        List<InterruptedException> exceptions = new ArrayList<>();
        workers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                exceptions.add(e);
            }
        });
        if (!exceptions.isEmpty()) {
            InterruptedException joinFail = new InterruptedException("Some threads were interrupted");
            exceptions.forEach(joinFail::addSuppressed);
            throw joinFail;
        }

        return reduce.apply(counted.stream());
    }

    /**
     * Join values to string.
     *
     * @param threads number of concurrent threads.
     * @param values  values to join.
     * @return list of joined result of {@link #toString()} call on each value.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.map(Object::toString).collect(Collectors.joining()),
                stream -> stream.collect(Collectors.joining()));
    }

    /**
     * Filters values by predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to filter.
     * @param predicate filter predicate.
     * @return list of values satisfying given predicated. Order of values is preserved.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.filter(predicate),
                stream -> stream.flatMap(Function.identity()).collect(Collectors.toList()));
    }

    /**
     * Maps values.
     *
     * @param threads number of concurrent threads.
     * @param values  values to filter.
     * @param f       mapper function.
     * @return list of values mapped by given function.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.map(f),
                stream -> stream.flatMap(Function.identity()).collect(Collectors.toList()));
    }

    /**
     * Returns maximum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @return maximum of given values
     * @throws InterruptedException   if executing thread was interrupted.
     * @throws NoSuchElementException if not values are given.
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.max(comparator).get(),
                stream -> stream.max(comparator).get());
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @return minimum of given values
     * @throws InterruptedException   if executing thread was interrupted.
     * @throws NoSuchElementException if not values are given.
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.min(comparator).get(),
                stream -> stream.min(comparator).get());
    }

    /**
     * Returns whether all values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @return whether all values satisfies predicate or {@code true}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(Boolean::booleanValue));
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @return whether any value satisfies predicate or {@code false}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate)
            throws InterruptedException {
        return commonTask(threads, values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(Boolean::booleanValue));
    }
}