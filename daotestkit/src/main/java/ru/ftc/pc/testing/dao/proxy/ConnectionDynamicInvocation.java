package ru.ftc.pc.testing.dao.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
class ConnectionDynamicInvocation implements InvocationHandler {
  private static final Logger log = LoggerFactory.getLogger(ConnectionDynamicInvocation.class);

  private final Connection connection;

  public ConnectionDynamicInvocation(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("prepareStatement")) {
      return ProxyFactory.createProxy((PreparedStatement) method.invoke(connection, args), PreparedStatement.class, PrepareStatementDynamicInvocation.class);
    }

    return method.invoke(connection, args);
  }
}