package ru.ftc.pc.testing.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
class PrepareStatementDynamicInvocation implements InvocationHandler {
  private final PreparedStatement statement;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("executeQuery")) {
      return ProxyFactory.createProxy((ResultSet) method.invoke(statement, args), ResultSet.class, ResultSetDynamicInvocation.class);
    }

    return method.invoke(statement, args);
  }
}