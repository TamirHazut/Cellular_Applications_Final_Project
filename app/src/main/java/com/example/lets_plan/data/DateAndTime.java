package com.example.lets_plan.data;

import java.util.Locale;

public class DateAndTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public DateAndTime() {
    }

    public int getYear() {
        return year;
    }

    public DateAndTime setYear(Long year) {
        return setYear(year.intValue());
    }
    public DateAndTime setMonth(Long month) { return setMonth(month.intValue()); }
    public DateAndTime setDay(Long day) {
        return setDay(day.intValue());
    }
    public DateAndTime setHour(Long hour) {
        return setHour(hour.intValue());
    }
    public DateAndTime setMinute(Long minute) {
        return setMinute(minute.intValue());
    }

    public DateAndTime setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public DateAndTime setMonth(int month) {
        this.month = month;
        return this;
    }

    public int getDay() {
        return day;
    }

    public DateAndTime setDay(int day) {
        this.day = day;
        return this;
    }

    public int getHour() {
        return hour;
    }

    public DateAndTime setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public DateAndTime setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%02d/%02d/%04d, %02d:%02d", day, month, year, hour, minute);
    }
}
