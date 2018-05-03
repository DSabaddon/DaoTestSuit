package ru.mds.component;

import org.springframework.stereotype.Component;
import ru.mds.model.Row;
import ru.mds.model.TableDescription;

import java.util.List;
import java.util.Map;
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
          // TODO здесь ещё должны быть необходимые преобразования
          .append(tableDescription.getColumns().stream()
              .map(column -> column.getName() + " " + column.getDatatype())
              .collect(Collectors.joining(",")))
          .append("); ");
    }
    return createTablesScript.toString();
  }

  String composeInsertScripts(List<Row> rows) {
    StringBuilder insertScript = new StringBuilder();
    for (Row row : rows) {
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      for (Map.Entry data : row.getData().entrySet()) {
        columns.append(data.getKey()).append(",");
        values.append(data.getValue()).append(",");
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
}
