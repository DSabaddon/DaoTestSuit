package ru.mds.testing.dao.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MDS
 * @since 22.05.2018 (v1.0)
 */
@Slf4j
@Component
class PreliminaryParameterResolver {
  private static final String PARAMETER = ":(\\w+)";
  private static final String DELS_POSSIBLE = "\\s*";
  private static final String ARITHMETIC_SIGN = "[+-]";
  /**
   * Арифметическая операция (сложение или вычитание), вторым операндом которой является <b>подстановочная переменная</b>
   */
  private static final String OPERAND = ARITHMETIC_SIGN + DELS_POSSIBLE + PARAMETER;
  private static final Pattern OPERAND_PATTERN = Pattern.compile(OPERAND);

  /**
   * @param sql         исходный текст <code>SQL</code>-запроса
   * @param paramSource параметры запроса
   * @return преобразованный <code>SQL</code>-запрос в случае, если были найдены арифметические операции в исходном запросе;
   * исходный запрос в случае, если в нём не были найдены арифметические операции
   * @implNote <b>H2</b> не умеет обрабатывать арифметические операции, у которых один операнд <b>дата</b>, а другой - <b>подстановочная переменная</b>.
   * Например, "<code>sysdate + :SEVRAL_DAYS</code>". Это выражение превращается в "<code>?1 + sysdate</code>"
   * и дальше выбрасывается исключение "<code>JdbcSQLException: Неизвестный тип данных: "?"</code>".<br/>
   * Ошибка кроется вот здесь: {@code org.h2.expression.Operation#optimize(org.h2.engine.Session)}.<br/>
   * Поэтому заранее подставляем значения для переменных, участвующих в арифметических операциях.<br/>
   * P.S.: "<code>INTEGER - TIMESTAMP</code>" не поддерживается, поэтому писать <code>:SEVRAL_DAYS + sysdate</code> нельзя.
   */
  String tryResolveParameter(String sql, SqlParameterSource paramSource) {
    log.trace("Пытаемся преобразовать переменные, участвующие в арифметических операциях");

    Matcher matcher = OPERAND_PATTERN.matcher(sql);

    while (matcher.find()) {
      String value = paramSource.getValue(matcher.group(1)).toString();
      sql = sql.replaceAll(":" + matcher.group(1), value);
      log.debug("Заменяем переменную '{}' на '{}'", matcher.group(1), value);
    }
    return sql;
  }
}