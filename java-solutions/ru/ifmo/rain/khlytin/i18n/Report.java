package ru.ifmo.rain.khlytin.i18n;

import java.util.ResourceBundle;

public class Report {
    private static final String HTML = "<html>";
    private static final String HTML_CLOSE = "</html>";
    private static final String HEAD = "<head>";
    private static final String HEAD_CLOSE = "</head>";
    private static final String META = "<meta charset=\"utf-8\">";
    private static final String TITLE = "<title>Text statistic</title>";
    private static final String BODY = "<body>";
    private static final String BODY_CLOSE = "</body>";
    private static final String H1 = "<h1>";
    private static final String H1_CLOSE = "</h1>";
    private static final String P = "<p>";
    private static final String P_CLOSE = "</p>";
    private static final String B = "<b>";
    private static final String B_CLOSE = "</b>";
    private static final String BR = "<br>\n";
    private static final String EOLN = "\n";

    private final ResourceBundle bundle;
    private final StatisticalData sentences;
    private final StatisticalData lines;
    private final StatisticalData words;
    private final StatisticalData numbers;
    private final StatisticalData currencies;
    private final StatisticalData dates;
    private final String textFile;

    Report(final ResourceBundle bundle, final StatisticalData sentences, final StatisticalData lines,
           final StatisticalData words, final StatisticalData numbers, final StatisticalData currencies,
           final StatisticalData dates, final String textFile) {
        this.bundle = bundle;
        this.sentences = sentences;
        this.lines = lines;
        this.words = words;
        this.numbers = numbers;
        this.currencies = currencies;
        this.dates = dates;
        this.textFile = textFile;
    }
    //should use messageFormat
    //: should be in bundles
    //one string is a very bad idea
    public String getReport() {
        return String.format(
                HTML + EOLN +
                        HEAD + EOLN +
                        META + EOLN +
                        TITLE + EOLN +
                        HEAD_CLOSE + EOLN +
                        BODY + EOLN +
                        H1 + "%s: %s" + H1_CLOSE + EOLN +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d" + BR +
                        "%s %s: %d" + BR +
                        "%s %s: %d" + BR +
                        "%s %s: %d" + BR +
                        "%s %s: %d" + BR +
                        "%s %s: %d" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %f" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %f" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %f" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s: %f" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s: %f" + BR + P_CLOSE +

                        P + B + "%s:" + B_CLOSE + BR +
                        "%s %s: %d (%d %s)" + BR +
                        "%s %s: %s" + BR +
                        "%s %s: %s" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %d (%s)" + BR +
                        "%s %s %s: %f" + BR + P_CLOSE +

                        BODY_CLOSE + EOLN +
                        HTML_CLOSE,

                bundle.getString("analyzedFile"),
                textFile,

                //

                bundle.getString("commonStatistics"),

                bundle.getString("Number"),
                bundle.getString("sentences"),
                sentences.occurrencesNumber,

                bundle.getString("Number"),
                bundle.getString("strings"),
                lines.occurrencesNumber,

                bundle.getString("Number"),
                bundle.getString("words"),
                words.occurrencesNumber,

                bundle.getString("Number"),
                bundle.getString("numbers"),
                numbers.occurrencesNumber,

                bundle.getString("Number"),
                bundle.getString("money"),
                currencies.occurrencesNumber,

                bundle.getString("Number"),
                bundle.getString("dates"),
                dates.occurrencesNumber,

                //

                bundle.getString("sentenceStatistics"),

                bundle.getString("Number"),
                bundle.getString("sentences"),
                sentences.occurrencesNumber,

                sentences.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("min"),
                bundle.getString("sentence"),
                sentences.minStringValue,

                bundle.getString("max"),
                bundle.getString("sentence"),
                sentences.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("sentenceRP"),
                (sentences.minLengthString != null) ?
                        sentences.minLengthString.length() : 0,

                sentences.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("sentenceRP"),
                (sentences.maxLengthString != null) ?
                        sentences.maxLengthString.length() : 0,

                sentences.maxLengthString,

                bundle.getString("middle"),
                bundle.getString("length"),
                bundle.getString("sentenceRP"),
                sentences.average,

                //

                bundle.getString("stringStatistics"),

                bundle.getString("Number"),
                bundle.getString("strings"),
                lines.occurrencesNumber,

                lines.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("minimal"),
                bundle.getString("string"),
                lines.minStringValue,

                bundle.getString("maximal"),
                bundle.getString("string"),
                lines.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("stringRP"),
                (lines.minLengthString != null) ?
                        lines.minLengthString.length() : 0,

                lines.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("stringRP"),
                (lines.maxLengthString != null) ?
                        lines.maxLengthString.length() : 0,

                lines.maxLengthString,

                bundle.getString("middle"),
                bundle.getString("length"),
                bundle.getString("stringRP"),
                lines.average,

                //

                bundle.getString("wordStatistics"),

                bundle.getString("Number"),
                bundle.getString("words"),
                words.occurrencesNumber,

                words.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("min"),
                bundle.getString("word"),
                words.minStringValue,

                bundle.getString("max"),
                bundle.getString("word"),
                words.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("wordRP"),
                (words.minLengthString != null) ?
                        words.minLengthString.length() : 0,

                words.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("wordRP"),
                (words.maxLengthString != null) ?
                        words.maxLengthString.length() : 0,

                words.maxLengthString,

                bundle.getString("middle"),
                bundle.getString("length"),
                bundle.getString("wordRP"),
                words.average,

                //

                bundle.getString("numberStatistics"),

                bundle.getString("Number"),
                bundle.getString("numbers"),
                numbers.occurrencesNumber,

                numbers.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("min"),
                bundle.getString("number"),
                numbers.minStringValue,

                bundle.getString("max"),
                bundle.getString("number"),
                numbers.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("numberRP"),
                (numbers.minLengthString != null) ?
                        numbers.minLengthString.length() : 0,

                numbers.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("numberRP"),
                (numbers.maxLengthString != null) ?
                        numbers.maxLengthString.length() : 0,

                numbers.maxLengthString,

                bundle.getString("mid"),
                bundle.getString("number"),
                numbers.average,

                //

                bundle.getString("moneyStatistics"),

                bundle.getString("Number"),
                bundle.getString("money"),
                currencies.occurrencesNumber,

                currencies.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("min"),
                bundle.getString("value"),
                currencies.minStringValue,

                bundle.getString("max"),
                bundle.getString("value"),
                currencies.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("money"),
                (currencies.minLengthString != null) ?
                        currencies.minLengthString.length() : 0,

                currencies.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("money"),
                (currencies.maxLengthString != null) ?
                        currencies.maxLengthString.length() : 0,

                currencies.maxLengthString,

                bundle.getString("mid"),
                bundle.getString("value"),
                currencies.average,

                //

                bundle.getString("dateStatistics"),

                bundle.getString("Number"),
                bundle.getString("dates"),
                dates.occurrencesNumber,

                dates.diffValuesNumber,
                bundle.getString("unique"),

                bundle.getString("minimal"),
                bundle.getString("date"),
                dates.minStringValue,

                bundle.getString("maximal"),
                bundle.getString("date"),
                dates.maxStringValue,

                bundle.getString("minimal"),
                bundle.getString("length"),
                bundle.getString("dateRP"),
                (dates.minLengthString != null) ?
                        dates.minLengthString.length() : 0,

                dates.minLengthString,

                bundle.getString("maximal"),
                bundle.getString("length"),
                bundle.getString("dateRP"),
                (dates.maxLengthString != null) ?
                        dates.maxLengthString.length() : 0,

                dates.maxLengthString,

                bundle.getString("middle"),
                bundle.getString("length"),
                bundle.getString("dateRP"),
                dates.average);
    }
}
