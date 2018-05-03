package ru.mds.dbtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mds.component.DatabaseInitializer;
import ru.mds.component.H2ScriptComposer;
import ru.mds.component.OracleScriptParser;
import ru.mds.dbtest.dao.TestDao;
import ru.mds.model.Row;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author MDS
 * @since 03.05.2018 (1.0)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InAppDbTest {
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private DatabaseInitializer databaseInitializer;
  private TestDao sut;

  @Before
  public void setUp() throws IOException {
    databaseInitializer = new DatabaseInitializer(
        new OracleScriptParser(),
        new H2ScriptComposer(),
        namedParameterJdbcTemplate);
    databaseInitializer.initDb("/create_tables.sql");

    sut = new TestDao(namedParameterJdbcTemplate);
  }

  @Test
  public void createTable() {
    String tableName = "IN_APP";
    Row row = new Row(tableName)
        .add("COL1", 5)
        .add("COL2", "'D'");

    Row row2 = new Row(tableName)
        .add("COL1", 8)
        .add("COL2", "'E'");

    Row row3 = new Row(tableName)
        .add("COL1", 9)
        .add("COL2", "'F'");

    List<Row> rows = Arrays.asList(row, row2, row3);

    databaseInitializer.insertRows(rows);

    List<Map<String, Object>> table = sut.getAllData();

    assertEquals(BigDecimal.valueOf(5), table.get(0).get("COL1"));
    assertEquals("D", table.get(0).get("COL2"));
    assertEquals(BigDecimal.valueOf(8), table.get(1).get("COL1"));
    assertEquals("E", table.get(1).get("COL2"));
    assertEquals(BigDecimal.valueOf(9), table.get(2).get("COL1"));
    assertEquals("F", table.get(2).get("COL2"));
  }

  @Test
  public void createTable2() {
    String tableName = "IN_APP2";
    Row row = new Row(tableName)
        .add("COL1", 5)
        .add("COL2", "'D'");

    Row row2 = new Row(tableName)
        .add("COL1", 8)
        .add("COL2", "'E'");

    Row row3 = new Row(tableName)
        .add("COL1", 9)
        .add("COL2", "'F'");

    List<Row> rows = Arrays.asList(row, row2, row3);

    databaseInitializer.insertRows(rows);

    List<Map<String, Object>> table = sut.getAllData2();

    assertEquals(BigDecimal.valueOf(5), table.get(0).get("COL1"));
    assertEquals("D", table.get(0).get("COL2"));
    assertEquals(BigDecimal.valueOf(8), table.get(1).get("COL1"));
    assertEquals("E", table.get(1).get("COL2"));
    assertEquals(BigDecimal.valueOf(9), table.get(2).get("COL1"));
    assertEquals("F", table.get(2).get("COL2"));
  }
}
