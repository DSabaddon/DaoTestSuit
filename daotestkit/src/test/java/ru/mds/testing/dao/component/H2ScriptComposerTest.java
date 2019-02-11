package ru.mds.testing.dao.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author maksimenko
 * @since 01.08.2018
 */
class H2ScriptComposerTest {
  private static final Clock FIXED_CLOCK = Clock.fixed(LocalDateTime.of(2018, 8, 11, 10, 20, 30).toInstant(UTC), UTC);

  private StringBuilder builder;

  private H2ScriptComposer sut;

  @BeforeEach
  void setUp() {
    builder = new StringBuilder();

    sut = new H2ScriptComposer();
  }

  @Test
  @DisplayName("Вставка null")
  void prepareValueAndAppend_null() {
    String expected = "null,";

    sut.prepareValueAndAppend(null, builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка UUID")
  void prepareValueAndAppend_uuid() {
    String expected = "'35839090bf5641c4a0e5bed62e3a6ad5',";

    sut.prepareValueAndAppend(UUID.fromString("35839090-bf56-41c4-a0e5-bed62e3a6ad5"), builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка LocalDate")
  void prepareValueAndAppend_localDate() {
    String expected = "'2018-08-11',";

    sut.prepareValueAndAppend(LocalDate.now(FIXED_CLOCK), builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка LocalDateTime")
  void prepareValueAndAppend_localDateTime() {
    String expected = "'2018-08-11T10:20:30',";

    sut.prepareValueAndAppend(LocalDateTime.now(FIXED_CLOCK), builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка Enum")
  void prepareValueAndAppend_enum() {
    String expected = "'SOME_ENUM',";

    sut.prepareValueAndAppend(TestEnum.SOME_ENUM, builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка String")
  void prepareValueAndAppend_string() {
    String expected = "'any_string',";

    sut.prepareValueAndAppend("any_string", builder);

    assertEquals(expected, builder.toString());
  }

  @Test
  @DisplayName("Вставка объекта любого другого класса (например, int)")
  void prepareValueAndAppend_other() {
    String expected = "10,";

    sut.prepareValueAndAppend(10, builder);

    assertEquals(expected, builder.toString());
  }

  private enum TestEnum {
    SOME_ENUM
  }
}