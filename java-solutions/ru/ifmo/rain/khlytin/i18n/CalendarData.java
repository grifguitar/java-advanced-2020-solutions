package ru.ifmo.rain.khlytin.i18n;

import java.util.Calendar;

public class CalendarData {
    Calendar first;
    String second;

    CalendarData(Calendar first, String second) {
        this.first = first;
        this.second = second;
    }

    public Calendar getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}
