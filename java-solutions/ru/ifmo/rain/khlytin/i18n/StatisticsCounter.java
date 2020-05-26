package ru.ifmo.rain.khlytin.i18n;

import java.text.*;
import java.util.*;

public class StatisticsCounter {
    private final Locale locale;
    private final String text;

    StatisticsCounter(final String text, final Locale locale) {
        this.locale = locale;
        this.text = text;
    }

    public List<StatisticalData> getStatistics(String mode) {
        BreakIterator breakIterator;
        NumberFormat numberFormat = null, currencyFormat = null;
        DateFormat dateFormat = null;
        Collator cmp = Collator.getInstance(locale);
        switch (mode) {
            case "Sentence":
                breakIterator = BreakIterator.getSentenceInstance(locale);
                break;
            case "Line":
                breakIterator = BreakIterator.getLineInstance(locale);
                break;
            case "Word":
                breakIterator = BreakIterator.getWordInstance(locale);
                break;
            default:
                breakIterator = BreakIterator.getWordInstance(locale);
                numberFormat = NumberFormat.getNumberInstance(locale);
                currencyFormat = NumberFormat.getCurrencyInstance(locale);
                dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
                break;
        }
        breakIterator.setText(text);
        if (mode.equals("Sentence") || mode.equals("Line") || mode.equals("Word")) {
            StatisticalData result = new StatisticalData();
            Set<String> terms = new HashSet<>();
            int startIt = breakIterator.first();
            for (int it = breakIterator.next(); it != BreakIterator.DONE; startIt = it, it = breakIterator.next()) {
                String term;
                if (!mode.equals("Sentence")) {
                    term = text.substring(startIt, it).replaceAll("\\s", "");
                } else {
                    term = text.substring(startIt, it);
                }
                if (term.isEmpty()) continue;
                if (result.occurrencesNumber == 0) {
                    result.minStringValue = term;
                    result.maxStringValue = term;
                    result.minLengthString = term;
                    result.maxLengthString = term;
                }

                result.occurrencesNumber++;
                if (!terms.contains(term)) {
                    result.diffValuesNumber++;
                    terms.add(term);
                }

                if (cmp.compare(result.minStringValue, term) > 0) {
                    result.minStringValue = term;
                }
                if (cmp.compare(result.maxStringValue, term) < 0) {
                    result.maxStringValue = term;
                }

                if (result.minLengthString.length() > term.length()) {
                    result.minLengthString = term;
                }
                if (result.maxLengthString.length() < term.length()) {
                    result.maxLengthString = term;
                }

                result.average += term.length();
            }
            result.average /= result.occurrencesNumber;
            List<StatisticalData> resultList = new ArrayList<>();
            resultList.add(result);
            return resultList;
        }

        List<Double> numbers = new ArrayList<>();
        List<CurrencyData> currencies = new ArrayList<>();
        List<CalendarData> dates = new ArrayList<>();
        int startIt = breakIterator.first();
        for (int it = breakIterator.next(); it != BreakIterator.DONE; startIt = it, it = breakIterator.next()) {
            String term = text.substring(startIt, it);
            try {
                Date date = dateFormat.parse(term);
                Calendar calendar = Calendar.getInstance(locale);
                calendar.setTime(date);
                dates.add(new CalendarData(calendar, term));
                continue;
            } catch (ParseException e) {
                //ignore
            }
            try {
                Number currency = currencyFormat.parse(term);
                numbers.add(currency.doubleValue());
                currencies.add(new CurrencyData(currency, term));
                continue;
            } catch (ParseException e) {
                //ignore
            }
            try {
                Number number = numberFormat.parse(term);
                numbers.add(number.doubleValue());
            } catch (ParseException e) {
                //ignore
            }
        }
        List<StatisticalData> resultList = new ArrayList<>();
        resultList.add(getNumbersStatistics(numbers));
        resultList.add(getCurrenciesStatistics(currencies));
        resultList.add(getDatesStatistics(dates));
        return resultList;
    }

    private static StatisticalData getNumbersStatistics(List<Double> numbers) {
        StatisticalData result = new StatisticalData();
        if (numbers.isEmpty())
            return result;
        result.occurrencesNumber = numbers.size();
        result.diffValuesNumber = (int) numbers.stream().distinct().count();
        Double minValue = numbers.get(0);
        Double maxValue = numbers.get(0);
        result.minLengthString = (numbers.get(0) != Math.floor(numbers.get(0)) || Double.isInfinite(numbers.get(0)))
                ? numbers.get(0).toString() : Integer.toString(numbers.get(0).intValue());
        result.maxLengthString = (numbers.get(0) != Math.floor(numbers.get(0)) || Double.isInfinite(numbers.get(0)))
                ? numbers.get(0).toString() : Integer.toString(numbers.get(0).intValue());
        for (Double x : numbers) {
            if (x < minValue)
                minValue = x;
            if (x > maxValue)
                maxValue = x;
            String s;
            if (x != Math.floor(x) || Double.isInfinite(x)) {
                s = x.toString();
            } else {
                s = Integer.toString(x.intValue());
            }
            if (s.length() < result.minLengthString.length())
                result.minLengthString = s;
            if (s.length() > result.maxLengthString.length())
                result.maxLengthString = s;
            result.average += x;
        }
        result.minStringValue = minValue.toString();
        result.maxStringValue = maxValue.toString();
        result.average /= result.occurrencesNumber;
        return result;
    }

    private static StatisticalData getCurrenciesStatistics(List<CurrencyData> currencies) {
        StatisticalData result = new StatisticalData();
        if (currencies.isEmpty())
            return result;
        result.occurrencesNumber = currencies.size();
        result.diffValuesNumber = (int) currencies.stream().distinct().count();
        double minValue = currencies.get(0).first.doubleValue();
        double maxValue = currencies.get(0).first.doubleValue();
        result.minLengthString = currencies.get(0).second;
        result.maxLengthString = currencies.get(0).second;
        for (CurrencyData data : currencies) {
            if (data.first.doubleValue() < minValue) {
                minValue = data.first.doubleValue();
                result.minStringValue = data.second;
            }
            if (data.first.doubleValue() > maxValue) {
                maxValue = data.first.doubleValue();
                result.maxStringValue = data.second;
            }
            if (data.second.length() < result.minLengthString.length()) {
                result.minLengthString = data.second;
            }
            if (data.second.length() > result.maxLengthString.length()) {
                result.maxLengthString = data.second;
            }
            result.average += data.first.doubleValue();
        }
        result.average /= result.occurrencesNumber;
        return result;
    }

    private static StatisticalData getDatesStatistics(List<CalendarData> dates) {
        StatisticalData result = new StatisticalData();
        if (dates.isEmpty())
            return result;
        result.occurrencesNumber = dates.size();
        result.diffValuesNumber = (int) dates.stream().distinct().count();
        Calendar minValue = dates.get(0).first;
        Calendar maxValue = dates.get(0).first;
        result.minLengthString = dates.get(0).second;
        result.maxLengthString = dates.get(0).second;
        for (CalendarData data : dates) {
            if (data.first.before(minValue)) {
                minValue = data.first;
                result.minStringValue = data.second;
            }
            if (data.first.after(maxValue)) {
                maxValue = data.first;
                result.maxStringValue = data.second;
            }
            if (data.second.length() < result.minLengthString.length()) {
                result.minLengthString = data.second;
            }
            if (data.second.length() > result.maxLengthString.length()) {
                result.maxLengthString = data.second;
            }
            result.average += data.second.length();
        }
        result.average /= result.occurrencesNumber;
        return result;
    }
}
