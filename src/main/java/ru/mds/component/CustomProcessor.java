package ru.mds.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.mds.proxy.DataSourceDynamicInvocation;
import ru.mds.proxy.ProxyFactory;

import javax.sql.DataSource;

/**
 * @author maksimenko
 * @since 24.07.2018 (vX.X.X)
 */
@Component
public class CustomProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      try {
        return ProxyFactory.createProxy((DataSource) bean, DataSource.class, DataSourceDynamicInvocation.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return bean;
  }
}