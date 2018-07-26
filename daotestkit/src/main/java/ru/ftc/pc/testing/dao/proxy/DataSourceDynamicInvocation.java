package ru.ftc.pc.testing.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DataSourceDynamicInvocation implements InvocationHandler {
  private final DataSource dataSource;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("getConnection")) {
      return ProxyFactory.createProxy((Connection) method.invoke(dataSource, args), Connection.class, ConnectionDynamicInvocation.class);
    }

    return method.invoke(dataSource, args);
  }
}