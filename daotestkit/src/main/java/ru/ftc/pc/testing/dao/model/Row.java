package ru.ftc.pc.testing.dao.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MDS
 * @since 03.05.2018 (v1.0)
 */
@Getter
@RequiredArgsConstructor
public class Row {
  private final String tableName;
  private final Map<String, Object> data = new HashMap<>();

  public Row withColumn(String column, Object value) {
    data.put(column, value);
    return this;
  }
}