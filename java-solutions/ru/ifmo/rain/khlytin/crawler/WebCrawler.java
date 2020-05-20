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

    public static void main(String[] args) {
        if (args == null || args.length == 0 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: url [depth [downloads [extractors [perHost]]]]");
        } else {
            try {
                int depth = (1 < args.length) ? Integer.parseInt(args[1]) : 1;
                int downloaders = (2 < args.length) ? Integer.parseInt(args[2]) : 1;
                int extractors = (3 < args.length) ? Integer.parseInt(args[3]) : 1;
                int perHost = (4 < args.length) ? Integer.parseInt(args[4]) : 1;

                try (WebCrawler crawler = new WebCrawler(new CachingDownloader(), downloaders, extractors, perHost)) {
                    crawler.download(args[0], depth);
                } catch (IOException e) {
                    System.err.println("Failed to initialize downloader: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.err.println("Only numeric arguments expected");
            }
        }
    }

    private class HostData {
        private final Queue<Runnable> waitingTasks;
        private int count;

        public HostData() {
            waitingTasks = new ArrayDeque<>();
            count = 0;
        }

        public synchronized void addTask(Runnable task) {
            waitingTasks.add(task);
            checkedCall(false);
        }

        private synchronized void callNext() {
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

        private synchronized void checkedCall(boolean finished) {
            if (finished) {
                count--;
            }
            if (count < perHost) {
                callNext();
            }
        }
    }

    private void queueDownload(final String link, final int depth, final Phaser level,
                               final Set<String> win, final ConcurrentMap<String, IOException> lose,
                               final ConcurrentLinkedQueue<String> waitingQueue) {
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
                    queueExtraction(document, level, waitingQueue);
                }
            } catch (IOException e) {
                lose.put(link, e);
            } finally {
                level.arrive();
            }
        });
    }

    private void queueExtraction(final Document document, final Phaser level,
                                 final ConcurrentLinkedQueue<String> waitingQueue) {
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

    //

    @Override
    public Result download(String url, int depth) {
        final ConcurrentLinkedQueue<String> waitingQueue = new ConcurrentLinkedQueue<>();
        final Set<String> win = ConcurrentHashMap.newKeySet();
        final ConcurrentMap<String, IOException> lose = new ConcurrentHashMap<>();
        final Phaser lock = new Phaser(1);

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
                    .forEach(link -> queueDownload(link, currentDepth, level, win, lose, waitingQueue));
            level.arriveAndAwaitAdvance();
        }
        lock.arrive();

        lock.arriveAndAwaitAdvance();
        return new Result(new ArrayList<>(win), lose);
    }

    @Override
    public void close() {
        extractorsPool.shutdownNow();
        downloadersPool.shutdownNow();
    }
}