package ru.mds.testing.dao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MDS
 * @since 01.05.2018 (v1.0)
 */
@Getter
@AllArgsConstructor
public class Column {
  private final String name;
  private final String datatype;
  private final boolean virtual;
  private final String defaultValue;
}