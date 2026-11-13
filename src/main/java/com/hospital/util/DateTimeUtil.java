package com.hospital.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Parsing and human-friendly formatting helpers for date and time inputs. */
public final class DateTimeUtil {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM uuuu");
    private DateTimeUtil() {}
    public static LocalDate parseDate(String value) { return LocalDate.parse(value.trim()); }
    public static LocalTime parseTime(String value) { return LocalTime.parse(value.trim()); }
    public static String displayDate(LocalDate value) { return DATE.format(value); }
}
