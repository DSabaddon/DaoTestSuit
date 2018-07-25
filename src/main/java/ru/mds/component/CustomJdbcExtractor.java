package ru.mds.component;

import org.springframework.jdbc.support.nativejdbc.SimpleNativeJdbcExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author MDS
 * @since 22.05.2018 (v1.0)
 */
class CustomJdbcExtractor extends SimpleNativeJdbcExtractor {
  @Override
  public ResultSet getNativeResultSet(ResultSet rs) {
    return new ProxyResultSet(rs) {
      @Override
      public <T> T getObject(String columnName, Class<T> type) throws SQLException {
        if (type.isEnum()) {
          String value = super.getObject(columnName, String.class);
          //noinspection unchecked
          return value == null ? null : (T) Enum.valueOf((Class<? extends Enum>) type, value);
        }
        return super.getObject(columnName, type);
      }
    };
  }
}
