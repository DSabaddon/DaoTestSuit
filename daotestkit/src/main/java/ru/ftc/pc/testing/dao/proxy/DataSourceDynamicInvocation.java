package ru.ftc.pc.testing.dao.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
public class DataSourceDynamicInvocation implements InvocationHandler {
  private static final Logger log = LoggerFactory.getLogger(DataSourceDynamicInvocation.class);

  private final DataSource dataSource;

  public DataSourceDynamicInvocation(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    log.info("Invoked method: {}", method.getName());

    if (method.getName().equals("getConnection")) {
      return ProxyFactory.createProxy((Connection) method.invoke(dataSource, args), Connection.class, ConnectionDynamicInvocation.class);
    }

    return method.invoke(dataSource, args);
  }
}