package ru.ifmo.rain.khlytin.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;

/**
 * @author khlyting
 */
public class RecursiveWalk {
    public static void main(String[] args) {
        try {
            if (args == null || args.length != 2) {
                throw new WalkerException("Two arguments expected.");
            }
            if (args[0] == null) {
                throw new WalkerException("Input file must be not null.");
            }
            if (args[1] == null) {
                throw new WalkerException("Output file must be not null.");
            }

            Path input, output;

            try {
                input = Paths.get(args[0]);
                output = Paths.get(args[1]);
            } catch (InvalidPathException e) {
                throw new WalkerException("Invalid input or output file name. " + e.getMessage());
            }

            try {
                if (output.getParent() != null)
                    Files.createDirectories(output.getParent());
            } catch (IOException e) {
                throw new WalkerException("Cannot create folder for output file. " + e.getMessage());
            }

            try (BufferedReader reader = Files.newBufferedReader(input)) {
                try (BufferedWriter writer = Files.newBufferedWriter(output)) {
                    HashingFileVisitor hashingFileVisitor = new HashingFileVisitor(writer);
                    for (String fileName = reader.readLine(); fileName != null; fileName = reader.readLine()) {
                        try {
                            Path filePath = Paths.get(fileName);
                            Files.walkFileTree(filePath, hashingFileVisitor);
                        } catch (InvalidPathException e) {
                            writer.write(String.format("%08x", 0) + " " + fileName);
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    throw new WalkerException("Cannot process the output or input file. " + e.getMessage());
                }
            } catch (IOException e) {
                throw new WalkerException("Cannot process input file. " + e.getMessage());
            }

        } catch (WalkerException e) {
            System.err.println(e.getMessage());
        }
    }
}