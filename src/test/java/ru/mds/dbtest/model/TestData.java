package ru.mds.dbtest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author MDS
 * @since 02.05.2018 (1.0)
 */
@AllArgsConstructor
@Getter
@ToString
public class TestData {
  private final Integer colNum;
  private final String colChar;
}
