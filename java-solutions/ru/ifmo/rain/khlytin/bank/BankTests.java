package ru.ifmo.rain.khlytin.bank;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class BankTests {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(Test.class);
        for (Failure failure : result.getFailures()) {
            System.err.println(failure.getTestHeader() + " : " + failure.getMessage());
        }
        if (!result.wasSuccessful()) {
            System.exit(1);
        }
        System.exit(0);
    }
}