package ru.ifmo.rain.khlytin.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

/**
 * Class implementing {@link ParallelMapper}.
 *
 * @author khlyting
 */
public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> workers;
    private final Queue<Runnable> tasks;
    private final static int MAX_TASKS = 1000000;

    private static class SyncCollector<R> {
        private List<R> results;
        private int count;

        SyncCollector(int size) {
            this.count = 0;
            this.results = new ArrayList<>(Collections.nCopies(size, null));
        }

        synchronized void syncSet(final int position, R element) {
            results.set(position, element);
            count++;
            if (count == results.size()) {
                notify();
            }
        }

        synchronized List<R> asList() throws InterruptedException {
            while (count < results.size()) {
                wait();
            }
            return results;
        }
    }

    /**
     * Thread-count constructor.
     * Creates a ParallelMapperImpl instance operating with maximum of {@code threads}
     * threads of type {@link Thread}.
     *
     * @param threads maximum count of operable threads
     */
    public ParallelMapperImpl(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("The number of threads must be positive");
        }
        Runnable runnable = () -> {
            try {
                while (!Thread.interrupted()) {
                    syncRunTask();
                }
            } catch (InterruptedException ignored) {
            } finally {
                Thread.currentThread().interrupt();
            }
        };
        this.tasks = new ArrayDeque<>();
        this.workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Thread(runnable));
        }
        for (Thread thread : workers) {
            thread.start();
        }
    }

    private void syncRunTask() throws InterruptedException {
        Runnable task;
        synchronized (tasks) {
            while (tasks.isEmpty()) {
                tasks.wait();
            }
            task = tasks.poll();
            tasks.notifyAll();
        }
        task.run();
    }

    private void syncAddTask(final Runnable task) throws InterruptedException {
        synchronized (tasks) {
            while (tasks.size() >= MAX_TASKS) {
                tasks.wait();
            }
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    /**
     * Maps function {@code f} over specified {@code args}.
     * Mapping for each element performs in parallel.
     *
     * @param f    function
     * @param args elements
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<RuntimeException> exceptionsList = new ArrayList<>();
        SyncCollector<R> collector = new SyncCollector<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            final int index = i;
            syncAddTask(() -> {
                R value = null;
                try {
                    value = f.apply(args.get(index));
                } catch (RuntimeException e) {
                    synchronized (exceptionsList) {
                        exceptionsList.add(e);
                    }
                }
                collector.syncSet(index, value);
            });
        }
        if (!exceptionsList.isEmpty()) {
            RuntimeException exception = new RuntimeException("Errors occurred while mapping some values");
            for (RuntimeException e : exceptionsList) {
                exception.addSuppressed(e);
            }
            throw exception;
        }
        return collector.asList();
    }

    /**
     * Stops all threads. All unfinished mappings leave in undefined state.
     */
    @Override
    public void close() {
        for (Thread worker : workers) {
            worker.interrupt();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException ignored) {
            }
        }
    }
}