package ru.ftc.pc.testing.dao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MDS
 * @since 01.05.2018 (v1.0)
 */
@AllArgsConstructor
@Getter
public class Column {
  private final String name;
  private final String datatype;
  private final boolean generated;
}