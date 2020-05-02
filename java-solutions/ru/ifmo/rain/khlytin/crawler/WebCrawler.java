package ru.ifmo.rain.khlytin.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author khlyting
 */
public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloadersPool;
    private final ExecutorService extractorsPool;
    private final int perHost;
    private final ConcurrentMap<String, HostData> hostMapper;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadersPool = Executors.newFixedThreadPool(downloaders);
        this.extractorsPool = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
        this.hostMapper = new ConcurrentHashMap<>();
    }

    private static int getByIndex(int index, String[] args) {
        if (index < args.length) {
            return Integer.parseInt(args[index]);
        }
        return 1;
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: url [depth [downloads [extractors [perHost]]]]");
        } else {
            try {
                int depth = getByIndex(1, args);
                int downloaders = getByIndex(2, args);
                int extractors = getByIndex(3, args);
                int perHost = getByIndex(4, args);

                try (Crawler crawler = new WebCrawler(new CachingDownloader(), downloaders, extractors, perHost)) {
                    crawler.download(args[0], depth);
                } catch (IOException e) {
                    System.err.println("Failed to initialize downloader: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.err.println("Only numeric arguments expected");
            }
        }
    }

    private class Handler {
    	//too big interface
        private final ConcurrentLinkedQueue<String> waitingQueue = new ConcurrentLinkedQueue<>();
        private final Set<String> win = ConcurrentHashMap.newKeySet();
        private final ConcurrentMap<String, IOException> lose = new ConcurrentHashMap<>();
        private final Phaser lock = new Phaser(1);

        public Handler(String url, int depth) {
            final Set<String> extracted = ConcurrentHashMap.newKeySet();
            waitingQueue.add(url);
            lock.register();
            for (int i = 0; i < depth; i++) {
                int currentDepth = depth - i;
                final Phaser level = new Phaser(1);
                List<String> processing = new ArrayList<>(waitingQueue);
                waitingQueue.clear();
                processing.stream()
                        .filter(extracted::add)
                        .forEach(link -> queueDownload(link, currentDepth, level));
                level.arriveAndAwaitAdvance();
            }
            lock.arrive();
        }

        public Result result() {
            lock.arriveAndAwaitAdvance();
            return new Result(new ArrayList<>(win), lose);
        }

        private void queueDownload(final String link, final int depth, final Phaser level) {
            String host;
            try {
                host = URLUtils.getHost(link);
            } catch (MalformedURLException e) {
                lose.put(link, e);
                return;
            }

            HostData hostData = hostMapper.computeIfAbsent(host, s -> new HostData());
            level.register();
            hostData.addTask(() -> {
                try {
                    Document document = downloader.download(link);
                    win.add(link);
                    if (depth > 1) {
                        queueExtraction(document, level);
                    }
                } catch (IOException e) {
                    lose.put(link, e);
                } finally {
                    level.arrive();
                }
            });
        }

        private void queueExtraction(final Document document, final Phaser level) {
            level.register();
            extractorsPool.submit(() -> {
                try {
                    List<String> links = document.extractLinks();
                    waitingQueue.addAll(links);
                } catch (IOException ignored) {
                } finally {
                    level.arrive();
                }
            });
        }
    }

    private class HostData {
        private final Queue<Runnable> waitingTasks;
        private int count;

        public HostData() {
            waitingTasks = new ArrayDeque<>();
            count = 0;
        }

        synchronized public void addTask(Runnable task) {
            waitingTasks.add(task);
            checkedCall(false);
        }

        synchronized private void callNext() {
            Runnable task = waitingTasks.poll();
            if (task != null) {
                count++;
                downloadersPool.submit(() -> {
                    try {
                        task.run();
                    } finally {
                        checkedCall(true);
                    }
                });
            }
        }

        synchronized private void checkedCall(boolean finished) {
            if (finished) {
                count--;
            }
            if (count < perHost) {
                callNext();
            }
        }
    }

    @Override
    public Result download(String url, int depth) {
        return new Handler(url, depth).result();
    }

    @Override
    public void close() {
        extractorsPool.shutdown();
        downloadersPool.shutdown();
        try {
            extractorsPool.awaitTermination(0, TimeUnit.MILLISECONDS);
            downloadersPool.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Could not terminate executor pools: " + e.getMessage());
        }
    }
}