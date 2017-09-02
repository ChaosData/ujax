package lib.ujax.models.magic;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;


import com.google.gson.Gson;
import java.lang.reflect.Constructor;

//import sun.reflect.ReflectionFactory;

public class GsonProxier {

  static final Objenesis objenesis = new ObjenesisStd(true);
/*
  //needs java agent
  static {
    ClassReloadingStrategy classReloadingStrategy =
        ClassReloadingStrategy.fromInstalledAgent();
    new ByteBuddy()
      .redefine(Gson.class)
      .modifiers(TypeManifestation.PLAIN)
      .make()
      .load(Gson.class.getClassLoader(), classReloadingStrategy);
    
  }
  */

//  public static Class<? extends Gson>
//  getProxyType(Class<? extends GsonInterceptor> clazz) {

  // public static Class<? extends Gson>
  // getProxyType(Class<? extends GsonInterceptor> clazz) {

  public static <T extends Gson & GsonProxy> Class<T>
  getProxyType(Class<? extends GsonInterceptor> clazz) {

    return Class.class.cast(new ByteBuddy()
      .subclass(Gson.class)
      .method(ElementMatchers.any())
      .intercept(MethodDelegation.to(clazz))
      .defineField("original", Gson.class, Visibility.PRIVATE)
      .implement(GsonProxy.class)
      .intercept(FieldAccessor.ofBeanProperty())
      .make()
      .load(GsonProxier.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
      .getLoaded());

  }

  //public static Constructor<? extends GsonProxy>
  public static <T extends Gson & GsonProxy> ObjectInstantiator<T>// extends GsonProxy>
  getProxyConstructor(Class<T> proxyType) {
    /*
    return (Constructor<? extends GsonProxy>)ReflectionFactory
        .getReflectionFactory()
        .newConstructorForSerialization(
          proxyType,
          Object.class.getDeclaredConstructor()
        )
    ;*/
    

    return objenesis.getInstantiatorOf(proxyType);
  }

  //public static Gson
  //wrap(Gson gson, Constructor<? extends GsonProxy> constructor) {

  //public static Gson
  //wrap(Gson gson, ObjectInstantiator<? extends GsonProxy> constructor) {
  public static <T extends Gson & GsonProxy> Gson
  wrap(Gson gson, ObjectInstantiator<T> constructor) {

    //GsonProxy proxy = constructor.newInstance();
    Object proxy = constructor.newInstance();
    ((GsonProxy)proxy).setOriginal(gson);
    return ((Gson) proxy);
  }

}