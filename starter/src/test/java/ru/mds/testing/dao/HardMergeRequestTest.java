package ru.mds.testing.dao;

import org.junit.jupiter.api.BeforeEach;
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
import java.sql.Date;
import java.sql.Types;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author a.poludov
 * @since 20.05.2019
 **/
class HardMergeRequestTest extends AbstractDaoTest {
  private static final Clock CLOCK = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));
  private static final String CONTRACT_ID = "123";
  private static final String LOYALTY_CODE = "<loyalty_code>";
  private static final String PACKAGE_CODE = "<package code>";

  private MergeRequestDao sut;

  @BeforeEach
  void setUp() throws IOException {
    sut = new MergeRequestDao(namedParameterJdbcTemplate);

    databaseInitializer.initDb("/create_tables_for_hard_merge_test.sql");

    databaseInitializer.insertRows(singletonList(
        new Row("FAVORITE_PACKAGE")
            .withColumn("CODE", PACKAGE_CODE)
            .withColumn("LOYALTY_CODE", LOYALTY_CODE)));
  }

  @Test
  void merge_notMatched() {
    Optional<FavoriteActivePackage> firstTimeResult = sut.findBy(CONTRACT_ID);
    assertFalse(firstTimeResult.isPresent());

    Date date = Date.valueOf(LocalDate.of(2019, 1, 10));

    sut.activate(CONTRACT_ID, new LoyaltyActiveAttribute(LOYALTY_CODE, date));

    Optional<FavoriteActivePackage> secondTimeResult = sut.findBy(CONTRACT_ID);
    assertTrue(secondTimeResult.isPresent());
    assertEquals(date, secondTimeResult.get().expirationDate);
    assertEquals(PACKAGE_CODE, secondTimeResult.get().packageCode);
    assertEquals(CONTRACT_ID, secondTimeResult.get().contractId);
  }

  //<editor-fold desc="matched">
  @Test
  void merge_matched_setNull() {
    databaseInitializer.insertRows(singletonList(
        new Row("FAVORITE_ACTIVE_PACKAGE")
            .withColumn("CONTRACT_ID", CONTRACT_ID)
            .withColumn("PACKAGE_CODE", PACKAGE_CODE)
            .withColumn("EXPIRED", LocalDate.now(CLOCK))));

    sut.activate(CONTRACT_ID, new LoyaltyActiveAttribute(LOYALTY_CODE, null));

    Optional<FavoriteActivePackage> secondTimeResult = sut.findBy(CONTRACT_ID);
    assertTrue(secondTimeResult.isPresent());
    assertNull(secondTimeResult.get().expirationDate);
    assertEquals(PACKAGE_CODE, secondTimeResult.get().packageCode);
    assertEquals(CONTRACT_ID, secondTimeResult.get().contractId);
  }

  @Test
  void merge_matched_setValue() {
    databaseInitializer.insertRows(singletonList(
        new Row("FAVORITE_ACTIVE_PACKAGE")
            .withColumn("CONTRACT_ID", CONTRACT_ID)
            .withColumn("PACKAGE_CODE", PACKAGE_CODE)
            .withColumn("EXPIRED", LocalDate.of(1999, 1, 1))));

    Date date = Date.valueOf(LocalDate.of(2019, 1, 10));

    sut.activate(CONTRACT_ID, new LoyaltyActiveAttribute(LOYALTY_CODE, date));

    Optional<FavoriteActivePackage> secondTimeResult = sut.findBy(CONTRACT_ID);
    assertTrue(secondTimeResult.isPresent());
    assertEquals(date, secondTimeResult.get().expirationDate);
    assertEquals(PACKAGE_CODE, secondTimeResult.get().packageCode);
    assertEquals(CONTRACT_ID, secondTimeResult.get().contractId);
  }
  //</editor-fold>

  @Test
  void merge_unknownCase() {
    Optional<FavoriteActivePackage> firstTimeResult = sut.findBy(CONTRACT_ID);
    assertFalse(firstTimeResult.isPresent());

    sut.activate(CONTRACT_ID, new LoyaltyActiveAttribute(LOYALTY_CODE + "some postfix", null));

    Optional<FavoriteActivePackage> secondTimeResult = sut.findBy(CONTRACT_ID);
    assertFalse(secondTimeResult.isPresent());
  }

  @Repository
  static class MergeRequestDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    MergeRequestDao(NamedParameterJdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    Optional<FavoriteActivePackage> findBy(String contractId) {
      String sql = "" +
          "SELECT CONTRACT_ID, PACKAGE_CODE, EXPIRED " +
          "FROM FAVORITE_ACTIVE_PACKAGE " +
          "WHERE CONTRACT_ID = :contractId";

      SqlParameterSource source = new MapSqlParameterSource()
          .addValue("contractId", contractId);

      try {
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, source, (rs, rowNum) -> new FavoriteActivePackage(
            rs.getString("CONTRACT_ID"),
            rs.getString("PACKAGE_CODE"),
            rs.getDate("EXPIRED"))));
      } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
      }
    }

    void activate(String contractId, LoyaltyActiveAttribute attributes) {
      String sql = "" +
          "MERGE INTO FAVORITE_ACTIVE_PACKAGE FAP " +
          "USING (SELECT :contractId CONTRACT_ID, FP.CODE CODE, :expired EXPIRED " +
          "       FROM FAVORITE_PACKAGE FP " +
          "       WHERE FP.LOYALTY_CODE = :loyaltyCode) REC " +
          "ON (FAP.CONTRACT_ID = REC.CONTRACT_ID AND FAP.PACKAGE_CODE = REC.CODE) " +
          "WHEN MATCHED THEN " +
          "  UPDATE " +
          "  SET " +
          "      EXPIRED = REC.EXPIRED " +
          "WHEN NOT MATCHED THEN " +
          "  INSERT (CONTRACT_ID, PACKAGE_CODE, EXPIRED) " +
          "  VALUES (REC.CONTRACT_ID, REC.CODE, REC.EXPIRED)";

      SqlParameterSource source = new MapSqlParameterSource()
          .addValue("contractId", new SqlParameterValue(Types.VARCHAR, contractId))
          .addValue("loyaltyCode", attributes.code, Types.VARCHAR)
          .addValue("expired", new SqlParameterValue(Types.DATE, attributes.expirationDate));

      jdbcTemplate.update(sql, source);
    }
  }

  static class LoyaltyActiveAttribute {
    private final String code;
    private final Date expirationDate;

    LoyaltyActiveAttribute(String code, Date expirationDate) {
      this.code = code;
      this.expirationDate = expirationDate;
    }
  }

  static class FavoriteActivePackage {
    private final String contractId;
    private final String packageCode;
    private final Date expirationDate;

    FavoriteActivePackage(String contractId,
                          String packageCode,
                          Date expirationDate) {
      this.contractId = contractId;
      this.packageCode = packageCode;
      this.expirationDate = expirationDate;
    }
  }
}