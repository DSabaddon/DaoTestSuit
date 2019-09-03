package ru.mds.testing.dao.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.springframework.jdbc.core.SqlReturnType.TYPE_UNKNOWN;

/**
 * @author a.poludov
 * @since 20.05.2019
 **/
@Slf4j
@Component
class MergeRequestResolver {
  private static final Pattern MERGE_REQUEST_PATTERN = Pattern.compile(
      "^\\s*MERGE\\s+INTO[\\s\\S]+USING\\s+\\(([\\s\\S]+?)\\)[\\s\\S]+ON[\\s\\S]+\\([\\s\\S]+",
      CASE_INSENSITIVE);
  private static final Pattern PARAMETER_PATTERN = Pattern.compile(":(\\w+)");
  private static final Map<Integer, String> MERGE_REQUEST_PARAM_MAP = new HashMap<>();

  static {
    MERGE_REQUEST_PARAM_MAP.put(Types.VARCHAR, "VARCHAR");
    MERGE_REQUEST_PARAM_MAP.put(Types.NUMERIC, "NUMBER");
    MERGE_REQUEST_PARAM_MAP.put(Types.DATE, "DATE");
    MERGE_REQUEST_PARAM_MAP.put(Types.BOOLEAN, "BOOLEAN");
  }

  /**
   * @param sql         исходный текст <code>SQL</code>-запроса
   * @param paramSource параметры запроса
   * @return преобразованный <code>SQL</code>-запрос в случае, если исходный запрос - это <code>MERGE</code>;
   * исходный запрос без изменений в противном случае
   * @implNote <b>H2</b> ломается на трансляции <code>MERGE</code>-запроса в случае, если именованные параметры
   * встречаются в <code>SELECT</code>-блоке. Ломается с ошибкой:<br/>
   * <code>Caused by: org.h2.jdbc.JdbcSQLNonTransientException: Unknown data type: "{param name}"; SQL statement:</code>.<br/>
   * Данный метод предназначен для того, чтобы помочь <b>H2</b> определить тип параметра, явно указав его в
   * запросе с помощью такого синтаксиса:<br/>
   * <code>CAST(:{paramName} AS {sqlTypeName})</code>
   * <p/>
   * Для того, чтобы этот подход работал, нужно явно указывать <code>SQL</code>-тип в исходном <code>paramSource</code>.
   * Например вот так:
   * <pre><code>
   * SqlParameterSource source = new MapSqlParameterSource()<br/>
   *   .addValue("name", new SqlParameterValue(java.sql.Types.VARCHAR, person.name))<br/>
   *   .addValue("age", new SqlParameterValue(java.sql.Types.NUMERIC, person.age));
   * </code></pre>
   * или так:
   * <pre><code>
   * SqlParameterSource source = new MapSqlParameterSource()<br/>
   *   .addValue("name", person.name, java.sql.Types.VARCHAR))<br/>
   *   .addValue("age", person.age, java.sql.Types.NUMERIC))<br/>
   * </code></pre>
   */
  String tryResolveMergeRequest(String sql, SqlParameterSource paramSource) {
    Matcher mergeRequestMatcher = MERGE_REQUEST_PATTERN.matcher(sql);
    if (!mergeRequestMatcher.matches()) {
      return sql;
    }
    log.trace("Пытаемся преобразовать merge-запрос");

    String selectBlock = mergeRequestMatcher.group(1);
    log.trace("Выделен SELECT-блок: '{}'", selectBlock);

    Matcher matcher = PARAMETER_PATTERN.matcher(selectBlock);
    while (matcher.find()) {
      String parameterName = matcher.group(1);
      int sqlType = paramSource.getSqlType(parameterName);
      if (sqlType == TYPE_UNKNOWN) {
        log.trace("Не удалось определить тип для параметра '{}'", parameterName);
        continue;
      }

      log.trace("Будет выполнена замена параметра '{}' на SQL-тип под номером '{}'", parameterName, sqlType);
      selectBlock = selectBlock.replaceAll(
          ":" + parameterName,
          format("CAST(:%s AS %s)", parameterName, MERGE_REQUEST_PARAM_MAP.get(sqlType)));
    }

    String beforeSelectBlock = sql.substring(0, mergeRequestMatcher.start(1));
    String afterSelectBlock = sql.substring(mergeRequestMatcher.end(1));
    return beforeSelectBlock + selectBlock + afterSelectBlock;

  }
}