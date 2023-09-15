package com.adrian.ryanair.flightinterconnector.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class YearSchedule {

    private int year;
    private List<MonthSchedule> months;

    public YearSchedule(int year) {
        this.year = year;
        this.months = new ArrayList<>();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonths(List<MonthSchedule> months) {
        this.months = months;
    }

    public List<MonthSchedule> getMonths() {
        return months;
    }

    public void addMonth(MonthSchedule monthSchedule) {
        months.add(monthSchedule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearSchedule that = (YearSchedule) o;
        return year == that.year && Objects.equals(months, that.months);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, months);
    }
}
