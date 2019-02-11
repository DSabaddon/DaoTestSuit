package ru.mds.testing.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ConnectionProxy implements InvocationHandler {
  private final Connection connection;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.trace("Через прокси вызван метод: {}", method.getName());

    if (method.getName().equals("prepareStatement")) {
      return ProxyFactory.createProxy((PreparedStatement) method.invoke(connection, args), PreparedStatement.class, PrepareStatementProxy.class);
    }

    return method.invoke(connection, args);
  }
}