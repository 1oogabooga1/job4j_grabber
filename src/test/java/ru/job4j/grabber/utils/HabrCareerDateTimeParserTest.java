package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class HabrCareerDateTimeParserTest {
    @Test
    void whenIsoOffsetDateTime() {
        String isoOffsetDateTime = "2011-12-03T10:15:30+01:00";
        HabrCareerDateTimeParser pars = new HabrCareerDateTimeParser();
        LocalDateTime rsl = pars.parse(isoOffsetDateTime);
        assertThat(rsl).isEqualTo("2011-12-03T10:15:30");
    }
}