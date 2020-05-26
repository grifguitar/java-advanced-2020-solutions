package ru.ifmo.rain.khlytin.i18n;

import org.junit.BeforeClass;
import org.junit.Assert;

public class Test {
    @BeforeClass
    public static void beforeClass() throws Exception {
    }

    @org.junit.Test
    public void test1() {
        String[] args = {"en_US", "ru_RU", "java-solutions\\ru\\ifmo\\rain\\khlytin\\i18n\\input.txt",
                "java-solutions\\ru\\ifmo\\rain\\khlytin\\i18n\\output.html"};
        TextStatistics.main(args);
    }
}
