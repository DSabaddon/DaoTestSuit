package ru.mds.component;

import org.springframework.stereotype.Component;
import ru.mds.model.Column;
import ru.mds.model.Row;
import ru.mds.model.TableDescription;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author MDS
 * @since 03.05.2018 (1.0)
 */
@Component
public class H2ScriptComposer {
  String composeDropWithCreateTableScripts(List<TableDescription> descriptions) {
    StringBuilder createTablesScript = new StringBuilder();
    for (TableDescription tableDescription : descriptions) {
      createTablesScript
          .append("DROP TABLE IF EXISTS ")
          .append(tableDescription.getName())
          .append(";")
          .append("CREATE TABLE ")
          .append(tableDescription.getName())
          .append(" (")
          .append(composeColumnsDefinition(tableDescription.getColumns()))
          .append("); ");
    }
    return createTablesScript.toString();
  }

  private String composeColumnsDefinition(List<Column> columns) {
    return columns.stream()
        .map(this::composeColumnDefinition)
        .collect(Collectors.joining(","));
  }

  private String composeColumnDefinition(Column column) {
    if (column.isGenerated()) {
      // TODO обработка не стандартных процедур
      return column.getName() + " VARCHAR2(256) AS " + column.getDatatype();
    } else {
      return column.getName() + " " + column.getDatatype();
    }
  }

  String composeInsertScripts(List<Row> rows) {
    StringBuilder insertScript = new StringBuilder();
    for (Row row : rows) {
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      for (Map.Entry data : row.getData().entrySet()) {
        columns.append(data.getKey()).append(",");
        prepareValueAndAppend(data.getValue(), values);
      }
      columns.deleteCharAt(columns.length() - 1);
      values.deleteCharAt(values.length() - 1);
      insertScript
          .append("INSERT INTO ")
          .append(row.getTableName())
          .append(" (")
          .append(columns)
          .append(") VALUES (")
          .append(values)
          .append("); ");
    }
    return insertScript.toString();
  }

  private void prepareValueAndAppend(Object value, StringBuilder values) {
    if (Objects.isNull(value)) {
      values.append((Object) null);
    } else if (value instanceof UUID) {
      values.append("'").append(value.toString().replaceAll("-", "")).append("'");
    } else if (value instanceof java.sql.Date) {
      values.append("'").append(value.toString()).append("'");
    } else if (value instanceof String) {
      values.append("'").append(value).append("'");
    } else {
      values.append(value);
    }
    values.append(",");
  }
}