package ru.ftc.pc.testing.dao.component;

import org.springframework.stereotype.Component;
import ru.ftc.pc.testing.dao.model.Column;
import ru.ftc.pc.testing.dao.model.TableDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author MDS
 * @since 30.04.2018 (v1.0)
 */
@Component
public class OracleScriptParser {
  private static final String DELIMITER = "\\s";
  private static final String DELS_POSSIBLE = DELIMITER + "*";
  private static final String DELS_NECESSARILY = DELIMITER + "+";

  /**
   * слово GENERATED ALWAYS AS (что-то) VIRTUAL
   */
  private static final String GENERATED_COLUMN = "(\\w+)" + DELS_NECESSARILY + "GENERATED ALWAYS AS" + DELS_POSSIBLE + "\\(([\\s\\S]*?)\\) VIRTUAL";
  private static final Pattern GENERATED_COLUMN_PATTERN = compile(GENERATED_COLUMN);

  /**
   * слово | слово() | слово(число) | слово(число, число...)
   */
  private static final String DATATYPE = "\\w+(?:\\([\\d, ]+\\))?";
  /**
   * DEFAULT sysdate | sys_guid() | null | число
   * TODO есть ещё DEFAULT RECHARGE_LINK_SEQ.nextval,
   */
  private static final String DEFAULT = "DEFAULT \\w+(?:\\(\\))?";
  private static final String COLUMN = "(\\w+)" + DELS_NECESSARILY + "(" + DATATYPE + DELS_POSSIBLE + "(?:" + DEFAULT + ")?" + ")";
  private static final Pattern COLUMN_PATTERN = compile(COLUMN);

  private static final Pattern TABLE_PATTERN = compile(
      "CREATE TABLE (\\w+) \\(" + DELS_POSSIBLE + "([\\s\\S]*?)" + "\\)(?: TABLESPACE \\w+)?;"
  ); // todo партиционирование

  List<TableDescription> parseCreateTable(String oracleScript) {
    List<TableDescription> tableDescriptions = new ArrayList<>();
    Matcher tableMatcher = TABLE_PATTERN.matcher(oracleScript);
    while (tableMatcher.find()) {
      List<Column> columns = new ArrayList<>();
      String tableName = tableMatcher.group(1).toUpperCase();
      String columnsDefinition = tableMatcher.group(2);
      Matcher generatedColumnMatcher = GENERATED_COLUMN_PATTERN.matcher(columnsDefinition);
      while (generatedColumnMatcher.find()) {
        String columnName = generatedColumnMatcher.group(1).toUpperCase();
        String columnDatatype = generatedColumnMatcher.group(2);
        columns.add(new Column(columnName, columnDatatype, true));
      }
      columnsDefinition = columnsDefinition.replaceAll(GENERATED_COLUMN, "");
      Matcher columnMatcher = COLUMN_PATTERN.matcher(columnsDefinition);
      while (columnMatcher.find()) {
        String columnName = columnMatcher.group(1).toUpperCase();
        String columnDatatype = columnMatcher.group(2);
        columns.add(new Column(columnName, columnDatatype, false));
      }
      tableDescriptions.add(new TableDescription(tableName, columns));
    }
    return tableDescriptions;
  }
}