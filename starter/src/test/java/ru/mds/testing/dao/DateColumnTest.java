package ru.mds.testing.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author apoludov
 * @since 20.05.2019
 **/
class DateColumnTest extends AbstractDaoTest {
  private static final String FULL_NAME_VALUE = "Bender Bending Rodriguez";

  private Dao sut;

  @BeforeEach
  void setUp() throws IOException {
    sut = new Dao(namedParameterJdbcTemplate);

    databaseInitializer.initDb("/create-tables-for-date-test.sql");
  }

  @Test
  void selectAndInsert() {
    Optional<Person> firstTimeResult = sut.findBy(FULL_NAME_VALUE);
    assertFalse(firstTimeResult.isPresent());

    Date date = Date.valueOf(LocalDate.of(2993, 9, 4));
    sut.insert(new Person(FULL_NAME_VALUE, date));

    Optional<Person> secondTimeResult = sut.findBy(FULL_NAME_VALUE);
    assertTrue(secondTimeResult.isPresent());
    assertEquals(FULL_NAME_VALUE, secondTimeResult.get().fullName);
    assertEquals(date, secondTimeResult.get().birthDate);
  }

  @Repository
  static class Dao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    Dao(NamedParameterJdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    void insert(Person person) {
      String sql = "" +
          "INSERT INTO PERSON (FULL_NAME, BIRTH_DATE) " +
          "VALUES (:fullName, :birthDate)";

      SqlParameterSource source = new MapSqlParameterSource()
          .addValue("fullName", person.fullName)
          .addValue("birthDate", person.birthDate);

      jdbcTemplate.update(sql, source);
    }

    Optional<Person> findBy(String fullName) {
      String sql = "" +
          "SELECT FULL_NAME, BIRTH_DATE " +
          "FROM PERSON " +
          "WHERE FULL_NAME = :fullName";

      SqlParameterSource source = new MapSqlParameterSource("fullName", fullName);

      try {
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, source, (rs, rowNum) -> new Person(
            rs.getString("FULL_NAME"),
            rs.getDate("BIRTH_DATE")
        )));
      } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
      }
    }
  }

  static class Person {
    final String fullName;
    final Date birthDate;

    Person(String fullName, Date birthDate) {
      this.fullName = fullName;
      this.birthDate = birthDate;
    }
  }
}