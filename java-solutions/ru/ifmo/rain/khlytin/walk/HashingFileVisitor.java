package ru.ifmo.rain.khlytin.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author khlyting
 */
public class HashingFileVisitor extends SimpleFileVisitor<Path> {
    private BufferedWriter writer;

    HashingFileVisitor(BufferedWriter writer) {
        this.writer = writer;
    }

    private static int FNVHash(InputStream inputStream) throws IOException {
        final int FNV_32_INITIAL = 0x811c9dc5;
        final int FNV_32_PRIME = 0x01000193;
        int hash = FNV_32_INITIAL;
        for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
            hash *= FNV_32_PRIME;
            hash ^= c;
        }
        return hash;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        int hash = 0;
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(file))) {
            hash = FNVHash(inputStream);
        } catch (IOException e) {
            //do nothing
        } finally {
            writer.write(String.format("%08x", hash) + " " + file.toString());
            writer.newLine();
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        writer.write(String.format("%08x", 0) + " " + file.toString());
        writer.newLine();
        return FileVisitResult.CONTINUE;
    }
}