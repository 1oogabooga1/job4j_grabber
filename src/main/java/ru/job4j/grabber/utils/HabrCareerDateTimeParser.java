package ru.job4j.grabber.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return offsetDateTime.toLocalDateTime();
    }
}