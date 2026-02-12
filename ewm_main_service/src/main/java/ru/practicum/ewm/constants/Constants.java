package ru.practicum.ewm.constants;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMATE);
}
