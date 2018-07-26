package ru.ftc.pc.testing.dao.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
class ResultSetDynamicInvocation implements InvocationHandler {
  private static final Logger log = LoggerFactory.getLogger(ResultSetDynamicInvocation.class);

  private final ResultSet resultSet;

  public ResultSetDynamicInvocation(ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("getObject")) {
      String columnLabel = (String) args[0];
      Class type = (Class) args[1];
      if (type.isEnum()) {
        String value = resultSet.getObject(columnLabel, String.class);
        //noinspection unchecked
        return value == null ? null : Enum.valueOf((Class<? extends Enum>) type, value);
      }
    }
    return method.invoke(resultSet, args);
  }
}