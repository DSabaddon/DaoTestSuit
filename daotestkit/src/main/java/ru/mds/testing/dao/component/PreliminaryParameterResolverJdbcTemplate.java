package ru.mds.testing.dao.component;

import lombok.extern.slf4j.Slf4j;
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
 * todo Добавить поддержку MERGE запросов
 *
 * @author MDS
 * @since 22.05.2018 (v1.0)
 */
@Slf4j
@Primary
@Component
class PreliminaryParameterResolverJdbcTemplate extends NamedParameterJdbcTemplate {
  private static final String PARAMETER = ":(\\w+)";
  private static final String DELS_POSSIBLE = "\\s*";
  private static final String ARITHMETIC_SIGN = "[+-]";
  /**
   * Арифметическая операция (сложение или вычитание), вторым операндом которой является <b>подстановочная переменная</b>
   */
  private static final String OPERAND = ARITHMETIC_SIGN + DELS_POSSIBLE + PARAMETER;
  private static final Pattern OPERAND_PATTERN = Pattern.compile(OPERAND);

  @Autowired
  public PreliminaryParameterResolverJdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }

  /**
   * @implNote <b>H2</b> не умеет обрабатывать арифметические операции, у которых один операнд <b>дата</b>, а другой - <b>подстановочная переменная</b>.
   * Например, "<code>sysdate + :SEVRAL_DAYS</code>". Это выражение превращается в "<code>?1 + sysdate</code>"
   * и дальше выбрасывается исключение "<code>JdbcSQLException: Неизвестный тип данных: "?"</code>".<br/>
   * Ошибка кроется вот здесь: {@code org.h2.expression.Operation#optimize(org.h2.engine.Session)}.<br/>
   * Поэтому заранее подставляем значения для переменных, участвующих в арифметических операциях.<br/>
   * P.S.: "<code>INTEGER - TIMESTAMP</code>" не поддерживается, поэтому писать <code>:SEVRAL_DAYS + sysdate</code> нельзя.
   */
  @Override
  public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
    log.trace("Пытаемся преобразовать переменные, участвующие в арифметических операциях");
    Matcher matcher = OPERAND_PATTERN.matcher(sql);

    // todo вот тут лимит править надо

    while (matcher.find()) {
      String value = paramSource.getValue(matcher.group(1)).toString();
      sql = sql.replaceAll(":" + matcher.group(1), value);
      log.debug("Заменяем переменную '{}' на '{}'", matcher.group(1), value);
    }
    return super.query(sql, paramSource, rowMapper);
  }
}