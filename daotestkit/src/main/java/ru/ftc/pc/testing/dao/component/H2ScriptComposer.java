package ru.ftc.pc.testing.dao.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ftc.pc.testing.dao.model.Column;
import ru.ftc.pc.testing.dao.model.Row;
import ru.ftc.pc.testing.dao.model.TableDescription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author MDS
 * @since 03.05.2018 (v1.0)
 */
@Slf4j
@Component
class H2ScriptComposer {
  private static final Pattern PROCEDURE_PATTERN = Pattern.compile("\\w+\\((\\w+)\\)");

  //<editor-fold desc="Drop & Create">
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
          .append(");\n");
    }
    return createTablesScript.toString();
  }

  private String composeColumnsDefinition(List<Column> columns) {
    return columns.stream()
        .map(this::composeColumnDefinition)
        .collect(Collectors.joining(","));
  }

  private String composeColumnDefinition(Column column) {
    String name = column.getName();
    String datatype = column.getDatatype();
    String defaultValue = column.getDefaultValue() == null ? "" : " " + column.getDefaultValue();
    if (defaultValue.contains("nextval")) {
      defaultValue = "DEFAULT 1104";
      log.debug("Заменяем '{}' на '{}' для колонки '{}'", column.getDefaultValue(), defaultValue, name);
    }

    if (column.isVirtual()) {
      Matcher procedureMatcher = PROCEDURE_PATTERN.matcher(datatype);
      if (procedureMatcher.find()) {
        log.debug("Заменяем '{}' на '{}' для колонки '{}'", datatype, procedureMatcher.group(1), name);
        datatype = procedureMatcher.group(1);
      }
      return name + " VARCHAR2(256) AS " + datatype + defaultValue; // todo бред: default всегда нет, а datatype - это значение
    } else if (datatype.contains("DATE")) {
      return name + " " + datatype.replace("DATE", "TIMESTAMP") + defaultValue;
    } else {
      return name + " " + datatype + defaultValue;
    }
  }
  //</editor-fold>

  //<editor-fold desc="Insert">
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
          .append(");\n");
    }
    return insertScript.toString();
  }

  void prepareValueAndAppend(Object value, StringBuilder values) {
    if (Objects.isNull(value)) {
      values.append((Object) null);
    } else if (value instanceof UUID) {
      values.append("'").append(value.toString().replaceAll("-", "")).append("'");
    } else if (value instanceof LocalDate || value instanceof LocalDateTime) {
      values.append("'").append(value.toString()).append("'");
    } else if (value.getClass().isEnum()) {
      values.append("'").append(((Enum) value).name()).append("'");
    } else if (value instanceof String) {
      values.append("'").append(value).append("'");
    } else {
      values.append(value);
    }
    values.append(",");
  }
  //</editor-fold>
}