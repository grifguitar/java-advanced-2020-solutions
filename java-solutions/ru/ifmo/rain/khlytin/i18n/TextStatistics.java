package ru.ifmo.rain.khlytin.i18n;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class TextStatistics {
    private static Locale getLocale(String stringLocale, boolean isFilter) {
        Locale locale;
        try {
            locale = Arrays.stream(Locale.getAvailableLocales())
                    .filter(l -> !isFilter || l.getLanguage().equals("ru") || l.getLanguage().equals("en"))
                    .filter(l -> l.toString().equals(stringLocale)).findFirst().get();
        } catch (NoSuchElementException e) {
            System.err.println("Unsupported " + (isFilter ? "output " : "text ") +
                    "locale, please select one of the available locales: ");
            Arrays.stream(Locale.getAvailableLocales())
                    .filter(l -> !isFilter || l.getLanguage().equals("ru") || l.getLanguage().equals("en"))
                    .map(l -> l.toString() + " " + l.getDisplayName())
                    .sorted().forEach(System.err::println);
            throw new NoSuchElementException();
        }
        return locale;
    }

    public static void main(String[] args) {
        if (args == null || args.length != 4 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: <text locale> <output locale> <text file> <report file>");
            return;
        }

        Locale textLocale, outputLocale;
        try {
            textLocale = getLocale(args[0], false);
            outputLocale = getLocale(args[1], true);
        } catch (NoSuchElementException e) {
            return;
        }

        String textFile = args[2];
        String reportFile = args[3];

        String text;
        try {
            text = Files.readString(Paths.get(textFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to read input file: " + e.getMessage());
            return;
        } catch (InvalidPathException e) {
            System.err.println("Invalid path to input file: " + e.getMessage());
            return;
        }

        StatisticsCounter worker = new StatisticsCounter(text, textLocale);

        StatisticalData sentences = worker.getStatistics("Sentence").get(0);
        StatisticalData lines = worker.getStatistics("Line").get(0);
        StatisticalData words = worker.getStatistics("Word").get(0);
        List<StatisticalData> list = worker.getStatistics("other");

        StatisticalData numbers = list.get(0);
        StatisticalData currencies = list.get(1);
        StatisticalData dates = list.get(2);

        ResourceBundle bundle;
        if (outputLocale.getLanguage().equals("ru")) {
            bundle = ResourceBundle.getBundle("ru.ifmo.rain.khlytin.i18n.UsageResourceBundle_ru");
        } else if (outputLocale.getLanguage().equals("en")) {
            bundle = ResourceBundle.getBundle("ru.ifmo.rain.khlytin.i18n.UsageResourceBundle_en");
        } else {
            System.err.println("Unsupported output locale, please select one of the available locales.");
            return;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile, StandardCharsets.UTF_8));
            Report report = new Report(bundle, sentences, lines,
                    words, numbers, currencies, dates, textFile, writer);
            report.handle();
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write to output file: " + e.getMessage());
        }

    }
}
