package ru.mds.testing.dao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author MDS
 * @since 02.05.2018 (v1.0)
 */
@Getter
@AllArgsConstructor
public class TableDescription {
  private final String name;
  private final List<Column> columns;
}