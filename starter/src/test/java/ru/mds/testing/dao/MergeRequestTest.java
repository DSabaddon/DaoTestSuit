package ru.mds.testing.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.mds.testing.dao.model.Row;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author a.poludov
 * @since 19.05.2019
 **/
class MergeRequestTest extends AbstractDaoTest {
  private static final String FULL_NAME_VALUE = "John A. Zoidberg";
  private static final int AGE_VALUE = 20;

  private MergeRequestDao sut;

  @BeforeEach
  void setUp() throws IOException {
    sut = new MergeRequestDao(namedParameterJdbcTemplate);

    databaseInitializer.initDb("/create_tables_for_merge_test.sql");
  }

  @Test
  @DisplayName("Успешно добавлена новая запись")
  void merge_notMatched() {
    Optional<Person> firstTimeResult = sut.findBy(FULL_NAME_VALUE);
    assertFalse(firstTimeResult.isPresent());

    sut.merge(new Person(FULL_NAME_VALUE, AGE_VALUE));

    Optional<Person> secondTimeResult = sut.findBy(FULL_NAME_VALUE);
    assertTrue(secondTimeResult.isPresent());
    assertEquals(FULL_NAME_VALUE, secondTimeResult.get().name);
    assertEquals(AGE_VALUE, secondTimeResult.get().age);
  }

  @Test
  @DisplayName("Успешно обновлена существующая запись")
  void merge_matched() {
    databaseInitializer.insertRows(singletonList(
        new Row("PERSON")
            .withColumn("FULL_NAME", FULL_NAME_VALUE)
            .withColumn("AGE", 1)));

    sut.merge(new Person(FULL_NAME_VALUE, AGE_VALUE));

    Optional<Person> secondTimeResult = sut.findBy(FULL_NAME_VALUE);
    assertTrue(secondTimeResult.isPresent());
    assertEquals(FULL_NAME_VALUE, secondTimeResult.get().name);
    assertEquals(AGE_VALUE, secondTimeResult.get().age);
  }

  @Repository
  static class MergeRequestDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    MergeRequestDao(NamedParameterJdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    Optional<Person> findBy(String name) {
      String sql = "" +
          "SELECT FULL_NAME, AGE " +
          "FROM PERSON " +
          "WHERE FULL_NAME = :name";

      SqlParameterSource source = new MapSqlParameterSource()
          .addValue("name", name);

      try {
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, source, (rs, rowNum) -> new Person(
            rs.getString("FULL_NAME"),
            rs.getInt("AGE"))));
      } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
      }
    }

    void merge(Person person) {
      String sql = "" +
          "MERGE INTO PERSON P " +
          "  USING (SELECT :name FULL_NAME, :age AGE FROM DUAL) REC " +
          "  ON (P.FULL_NAME = REC.FULL_NAME) " +
          "WHEN MATCHED THEN " +
          "  UPDATE " +
          "    SET AGE = REC.AGE " +
          "WHEN NOT MATCHED THEN " +
          "  INSERT (FULL_NAME, AGE) " +
          "  VALUES (REC.FULL_NAME, REC.AGE)";

      SqlParameterSource source = new MapSqlParameterSource()
          .addValue("name", new SqlParameterValue(java.sql.Types.VARCHAR, person.name))
          .addValue("age", new SqlParameterValue(java.sql.Types.NUMERIC, person.age));

      jdbcTemplate.update(sql, source);
    }
  }

  static class Person {
    private final String name;
    private final int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}