package ru.mds.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MDS
 * @since 03.05.2018 (1.0)
 */
@RequiredArgsConstructor
@Getter
public class Row {
  private final String tableName;
  private Map<String, Object> data = new HashMap<>();

  public Row add(String column, Object value) {
    data.put(column, value);
    return this;
  }
}
