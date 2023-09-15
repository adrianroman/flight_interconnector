package com.adrian.ryanair.flightinterconnector.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Util {
    private Util() {}

    public static LocalDateTime timeToDate(int year, int month, int day, LocalTime localTime) {
        return LocalDateTime.of(LocalDate.of(year, month, day), localTime);
    }
}
