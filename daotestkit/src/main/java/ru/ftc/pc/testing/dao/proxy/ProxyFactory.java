package ru.ftc.pc.testing.dao.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author maksimenko
 * @since 25.07.2018 (v1.0)
 */
class ProxyFactory {
  static <T> Object createProxy(T object, Class<T> clazz, Class<? extends InvocationHandler> handlerClass)
      throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
    InvocationHandler handler = handlerClass.getConstructor(clazz).newInstance(object);

    return Proxy.newProxyInstance(
        ProxyFactory.class.getClassLoader(),
        new Class[]{clazz},
        handler
    );
  }
}