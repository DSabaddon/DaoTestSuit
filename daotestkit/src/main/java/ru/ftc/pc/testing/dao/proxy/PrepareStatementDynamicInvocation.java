package ru.ftc.pc.testing.dao.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
class PrepareStatementDynamicInvocation implements InvocationHandler {
  private static final Logger log = LoggerFactory.getLogger(PrepareStatementDynamicInvocation.class);

  private final PreparedStatement statement;

  public PrepareStatementDynamicInvocation(PreparedStatement statement) {
    this.statement = statement;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("executeQuery")) {
      return ProxyFactory.createProxy((ResultSet) method.invoke(statement, args), ResultSet.class, ResultSetDynamicInvocation.class);
    }

    return method.invoke(statement, args);
  }
}