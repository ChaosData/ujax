package lib.ujax.models.magic;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.implementation.bind.annotation.AllArguments;

import java.lang.reflect.Method;

public abstract class GsonInterceptor<T extends GsonProxyPrefix> {

  static final String ALLOWED_PREFIX = "gg";//new T().getAllowedPrefix();

  @RuntimeType
  public static Object intercept(@Origin(cache = true) Method method,
                                 @This GsonProxy proxy,
                                 @AllArguments Object[] arguments) 
                                     throws Exception {
    if (!method.getName().startsWith(ALLOWED_PREFIX)) {
      throw new UnsupportedOperationException();
    }
    return method.invoke(proxy.getOriginal(), arguments);
  }
}