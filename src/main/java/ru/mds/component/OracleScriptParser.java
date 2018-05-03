package ru.mds.component;

import org.springframework.stereotype.Component;
import ru.mds.model.Column;
import ru.mds.model.TableDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author MDS
 * @since 30.04.2018 (1.0)
 */
@Component
public class OracleScriptParser {
  private static final String DELIMITERS = "\\s*";

  private static final String COLUMN = "(\\w+)" + DELIMITERS + "(\\w+(?:\\(\\d+\\))?)";
  private static final String COLUMNS = "(?:" + COLUMN + ",?" + DELIMITERS + ")+"; // TODO default value
  private static final Pattern COLUMN_PATTERN = compile(COLUMN);
  private static final Pattern TABLE_PATTERN = compile(
      "CREATE TABLE (\\w+) \\(" + DELIMITERS + "(" + COLUMNS + ")\\);"
  ); // todo партиционирование

  List<TableDescription> parseCreateTable(String oracleScript) {
    List<TableDescription> tableDescriptions = new ArrayList<>();
    Matcher tableMatcher = TABLE_PATTERN.matcher(oracleScript);
    while (tableMatcher.find()) {
      List<Column> columns = new ArrayList<>();
      String tableName = tableMatcher.group(1).toUpperCase();
      Matcher columnMatcher = COLUMN_PATTERN.matcher(tableMatcher.group(2));
      while (columnMatcher.find()) {
        String columnName = columnMatcher.group(1).toUpperCase();
        String columnDatatype = columnMatcher.group(2).toUpperCase();
        columns.add(new Column(columnName, columnDatatype));
      }
      tableDescriptions.add(new TableDescription(tableName, columns));
    }
    return tableDescriptions;
  }
}
