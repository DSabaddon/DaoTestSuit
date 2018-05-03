package ru.mds.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author MDS
 * @since 02.05.2018 (1.0)
 */
@AllArgsConstructor
@Getter
public class TableDescription {
  private final String name;
  private final List<Column> columns;
}
