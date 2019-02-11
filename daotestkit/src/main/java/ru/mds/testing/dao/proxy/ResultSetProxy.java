package ru.mds.testing.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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
// todo беда, если колонка называется limit!
    if (isSuitableMethod(method)) {
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

  /**
   * Проверяет, является ли метод тем самым, который нужно преобразовывать.
   * Руками для преобразования Enum мы вызываем {@link ResultSet#getObject(String, Class)}. Только для него нужно особое поведение.<br/>
   * {@link BeanPropertyRowMapper} для преобразования Enum вызывает {@link ResultSet#getObject(int)}. Так что не все getObject одинаково хороши...
   */
  private boolean isSuitableMethod(Method method) {
    return method.getName().equals("getObject")
        && method.getParameterCount() == 2
        && method.getParameterTypes()[0] == String.class
        && method.getParameterTypes()[1] == Class.class;
  }
}