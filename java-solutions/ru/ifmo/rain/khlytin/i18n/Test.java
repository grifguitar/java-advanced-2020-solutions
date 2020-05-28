package ru.ifmo.rain.khlytin.i18n;

import org.junit.BeforeClass;
import org.junit.Assert;

import java.util.Locale;

public class Test {
    @BeforeClass
    public static void beforeClass() {
    }

    @org.junit.Test
    public void test2() {
        StatisticsCounter worker = new StatisticsCounter("$300", Locale.US);
        StatisticalData currencies = worker.getStatistics("other").get(1);
        Assert.assertEquals(1, currencies.occurrencesNumber);
        Assert.assertEquals(1, currencies.diffValuesNumber);
        Assert.assertNotNull(currencies.minLengthString);
        Assert.assertEquals(4, currencies.minLengthString.length());
        Assert.assertNotNull(currencies.maxLengthString);
        Assert.assertEquals(4, currencies.maxLengthString.length());
        Assert.assertEquals("$300", currencies.minStringValue);
        Assert.assertEquals("$300", currencies.maxStringValue);
        Assert.assertEquals(300, currencies.average, 0.0001);
    }

    @org.junit.Test
    public void test3() {
        StatisticsCounter worker = new StatisticsCounter("$300", new Locale("ru", "RU"));
        StatisticalData currencies = worker.getStatistics("other").get(1);
        Assert.assertNotEquals(1, currencies.occurrencesNumber);
        Assert.assertNotEquals(1, currencies.diffValuesNumber);
        Assert.assertNull(currencies.minLengthString);
        Assert.assertNull(currencies.maxLengthString);
        Assert.assertNotEquals("$300", currencies.minStringValue);
        Assert.assertNotEquals("$300", currencies.maxStringValue);
        Assert.assertNotEquals(300, currencies.average, 0.0001);
    }

    @org.junit.Test
    public void test4() {
        StatisticsCounter worker = new StatisticsCounter("26.05.2020", new Locale("ru", "RU"));
        StatisticalData dates = worker.getStatistics("other").get(2);
        Assert.assertEquals(1, dates.occurrencesNumber);
        Assert.assertEquals(1, dates.diffValuesNumber);
        Assert.assertNotNull(dates.minLengthString);
        Assert.assertEquals(10, dates.minLengthString.length());
        Assert.assertNotNull(dates.maxLengthString);
        Assert.assertEquals(10, dates.maxLengthString.length());
        Assert.assertEquals("26.05.2020", dates.minStringValue);
        Assert.assertEquals("26.05.2020", dates.maxStringValue);
    }

    @org.junit.Test
    public void test5() {
        StatisticsCounter worker = new StatisticsCounter("26.05.2020", Locale.US);
        StatisticalData dates = worker.getStatistics("other").get(2);
        Assert.assertNotEquals(1, dates.occurrencesNumber);
        Assert.assertNotEquals(1, dates.diffValuesNumber);
        Assert.assertNull(dates.minLengthString);
        Assert.assertNull(dates.maxLengthString);
        Assert.assertNotEquals("26.05.2020", dates.minStringValue);
        Assert.assertNotEquals("26.05.2020", dates.maxStringValue);
    }

    @org.junit.Test
    public void test6() {
        StatisticsCounter worker = new StatisticsCounter("1 2 3 2", Locale.US);
        StatisticalData numbers = worker.getStatistics("other").get(0);
        Assert.assertEquals(4, numbers.occurrencesNumber);
        Assert.assertEquals(3, numbers.diffValuesNumber);
        Assert.assertEquals(2, numbers.average, 0.0001);
    }

    @org.junit.Test
    public void test7() {
        StatisticsCounter worker = new StatisticsCounter("Hello big world", Locale.US);
        StatisticalData words = worker.getStatistics("Word").get(0);
        Assert.assertEquals(3, words.occurrencesNumber);
        Assert.assertEquals(3, words.diffValuesNumber);
        Assert.assertEquals("big", words.minStringValue);
        Assert.assertEquals("world", words.maxStringValue);
        Assert.assertNotNull(words.minLengthString);
        Assert.assertEquals(3, words.minLengthString.length());
        Assert.assertNotNull(words.maxLengthString);
        Assert.assertEquals(5, words.maxLengthString.length());
        Assert.assertEquals(4, words.average, 1);
    }
}
