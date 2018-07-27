package ru.ftc.pc.testing.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ResultSetProxy implements InvocationHandler {
  private final ResultSet resultSet;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.trace("Через прокси вызван метод: {}", method.getName());

    if (method.getName().equals("getObject")) {
      String columnLabel = (String) args[0];
      Class type = (Class) args[1];
      if (type.isEnum()) {
        log.debug("Преобразуем значение из колонки '{}' в Enum '{}'", columnLabel, type.getName());
        String value = resultSet.getObject(columnLabel, String.class);
        //noinspection unchecked
        return value == null ? null : Enum.valueOf((Class<? extends Enum>) type, value);
      }
    }
    return method.invoke(resultSet, args);
  }
}