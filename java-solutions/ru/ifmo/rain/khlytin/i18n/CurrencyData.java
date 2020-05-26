package ru.ifmo.rain.khlytin.i18n;

public class CurrencyData {
    Number first;
    String second;

    CurrencyData(Number first, String second) {
        this.first = first;
        this.second = second;
    }

    public Number getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}
