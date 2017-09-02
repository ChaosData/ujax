package lib.ujax.filter.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import java.util.function.*;

import java.lang.reflect.*;
import java.util.*;

import java.io.IOException;

import lib.ujax.filter.common.AfterFiltererException;
import lib.ujax.filter.common.AfterFilter;

public class AfterFilterer implements ContainerResponseFilter {

  //List<Method> methods = null;
  //Object instance = null;
  List<AfterFilter> filters = null;

  //public AfterFilterer(List<Method> _methods, Object _instance) {
  public AfterFilterer(List<AfterFilter> _filters)
  {
    //methods = _methods;
    //instance = _instance;
    filters = _filters;
  }

  @Override
  public void filter(ContainerRequestContext requestContext,
              ContainerResponseContext responseContext)
      throws IOException {
    //terrible hack to get around loop
    if (responseContext.getStatus() == 999) {
      responseContext.setStatus(500);
      return;
    }

    /*if (methods == null || instance == null) {
      return;
    }*/
    if (filters == null) {
      return;
    }

    //for (Method m : methods) {
    for (AfterFilter f : filters) {

      try {
        //m.invoke(instance, requestContext, responseContext);
        f.accept(requestContext, responseContext);
      } catch (RuntimeException re) {
        Throwable t = re.getCause();
        if (t instanceof InvocationTargetException) {
          ((InvocationTargetException)t)
              .getTargetException()
              .printStackTrace();
          throw new AfterFiltererException();
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new AfterFiltererException();
      }
    }
  }
}