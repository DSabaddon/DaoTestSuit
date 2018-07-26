package ru.ftc.pc.testing.dao.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MDS
 * @since 22.05.2018 (v1.0)
 */
@Primary
@Component
class CustomJdbcTemplate extends NamedParameterJdbcTemplate {
  private static final String ARITHMETIC_OPERATION_WITH_PARAMETERIZED_OPERAND = "[\\w()]+ [+-] :(\\w+)";
  private static final Pattern PATTERN = Pattern.compile(ARITHMETIC_OPERATION_WITH_PARAMETERIZED_OPERAND);// todo если несколько операций

  @Autowired
  public CustomJdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
    Matcher matcher = PATTERN.matcher(sql);
    // Этот ужас нужен, потому что H2 не умеет обрабатывать арифметические операции производимые с подстановочными переменными
    if (matcher.find()) {
      String value = paramSource.getValue(matcher.group(1)).toString();
      sql = sql.replaceAll(":" + matcher.group(1), value);
    }
    return super.query(sql, paramSource, rowMapper);
  }
}