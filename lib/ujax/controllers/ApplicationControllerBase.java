package lib.ujax.controllers;

import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import lib.ujax.render.Templater;
import lib.ujax.filter.common.*;

import java.lang.reflect.*;
import javax.ws.rs.container.*;
import java.util.*;
import java.util.function.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

public class ApplicationControllerBase {

  protected static boolean Only = true;
  protected static boolean Except = false;

  @Context
  protected HttpServletRequest request_;

  public ApplicationControllerBase() { }

  public static Map<String,Object> getSession(HttpServletRequest req) {
    return getSession(req, true);
  }


  @SuppressWarnings("unchecked")
  public static Map<String,Object> getSession(HttpServletRequest req,
                                                 boolean create) {
    Map<String,Object> session = null;
    try {
      session = (Map<String,Object>)req.getAttribute("session");
    } catch (ClassCastException e) { }
    if (session == null) {
      if (create) {
        session = new HashMap<String,Object>();
        req.setAttribute("session", session);
      }
    }
    return session;
  }


  public static Map<String,Object> getSession(ContainerRequestContext crc) {
    return getSession(crc, true);
  }

  @SuppressWarnings("unchecked")
  public static Map<String,Object> getSession(ContainerRequestContext crc,
                                                 boolean create) {
    Map<String,Object> session = null;
    try {
      session = (Map<String,Object>)crc.getProperty("session");
    } catch (ClassCastException e) { }
    if (session == null) {
      if (create) {
        session = new HashMap<String,Object>();
        crc.setProperty("session", session);
      }
    }
    return session;
  }

  @SuppressWarnings("unchecked")
  private void addCsrfToken(Map<String,Object> ctx) {
    Map<String,Object> session = (Map<String,Object>)request_.getAttribute("session");
    if (session != null) {
      String csrf_token = (String)session.get("csrf_token");
      if (csrf_token != null) {
        ctx.put("csrf_token", csrf_token);
      }
    }
  }

  public Response ok(String template_path, Map<String, Object> ctx,
      Map<String,String> headers, String type) {
    addCsrfToken(ctx);
    return Templater.ok(template_path, ctx, headers, type);
  }

  public Response ok(Map<String, Object> ctx,
      Map<String,String> headers, String type) {
    addCsrfToken(ctx);
    return Templater.ok(ctx, headers, type, this);
  }

  public Response err(String template_path, Map<String, Object> ctx,
      Map<String,String> headers, String type, int status) {
    addCsrfToken(ctx);
    return Templater.err(template_path, ctx, headers, type, status);
  }


  public Response err(Map<String, Object> ctx,
      Map<String,String> headers, String type, int status) {
    addCsrfToken(ctx);
    return Templater.err(ctx, headers, type, status, this);
  }










  //TODO: overhaul before/after filters so that any class.method can be called
  //      trivially. provbably want to pass in Consumer/BiConsumer as the
  //      filter and offer some overloads that will generate it.
  //      The annoying thing is going to storing them, since i'll need a struct
  //      to do it ( can't use Method[] :| )

  protected static Class<?> getCallerClass(int depth) {
    try {
      Class<?> callerClass =
        Class.forName(new Exception()
          .getStackTrace()[depth]
          .getClassName());
      if (callerClass == null) {
        System.err.println("before/after_filter: calling class forName -> null");
        return null;
      }
      return callerClass;
    } catch (Exception e) {
      return null;
    }
  }

  protected static Class<?> getCallerClass() {
    try {
      StackTraceElement[] stes = (new Exception()).getStackTrace();
      for (int i = 0; i < stes.length; i++) {
        Class<?> clazz = Class.forName(stes[i].getClassName());
        if (ApplicationControllerBase.class.isAssignableFrom(clazz) &&
            !ApplicationControllerBase.class.equals(clazz)) {
          return clazz;
        }
      }
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String[] getCallerClassAndMethod() {
    try {
      StackTraceElement[] stes = (new Exception()).getStackTrace();
      for (int i = 0; i < stes.length; i++) {
        Class<?> clazz = Class.forName(stes[i].getClassName());
        if (ApplicationControllerBase.class.isAssignableFrom(clazz) &&
            !ApplicationControllerBase.class.equals(clazz)) {
          return new String[]{stes[i].getClassName(), stes[i].getMethodName()};
        }
      }
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void before_action(String _filter) {
    before_action_string(getCallerClass(), _filter, Except, new String[]{ });
  }

  public static void before_action(String _filter, boolean _only,
                               String[] _actions) {
    before_action_string(getCallerClass(), _filter, _only, _actions);
  }

  private static void before_action_string(Class<?> _callerClass,
      String _filter, boolean _only, String[] _actions) {
    try {
      final Method filter_method
          = _callerClass.getMethod(_filter, ContainerRequestContext.class);

      BeforeFilter filter = (crc) -> {
        try {
          filter_method.invoke(null, crc);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };

      before_action(_callerClass, filter, _only, _actions);
    } catch (Exception e) {
      throw new RuntimeException("failed to find filter method '" + _filter
                                 + "(ContainerRequestContext)' in controller "
                                 + _callerClass);
    }
  }
  
  public static void before_action(Class<?> _class, String _method) {
    before_action_method(getCallerClass(), _class, _method, Except, new String[]{ });
  }

  public static void before_action(Class<?> _class, String _method,
                                      boolean _only, String[] _actions) {
    before_action_method(getCallerClass(), _class, _method, _only, _actions);
  }


  private static void before_action_method(Class<?> _callerClass,
      Class<?> _class, String _method, boolean _only, String[] _actions) {
    Method _filter = null;

    try {
      _filter = _class.getDeclaredMethod(_method, ContainerRequestContext.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    final Method flt = _filter;
    BeforeFilter filter = (crc) -> {
      try {
        flt.invoke(null, crc);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };

    before_action(_callerClass, filter, _only, _actions);
  }

  public static
  void before_action(BeforeFilter _filter) {
    before_action(getCallerClass(), _filter, Except, new String[]{ });    
  }

  public static
  void before_action(BeforeFilter _filter,
      boolean _only, String[] _actions) {
    before_action(getCallerClass(), _filter, _only, _actions);
  }


  private static void before_action(Class<?> _callerClass,
      BeforeFilter _filter,
      boolean _only, String[] _actions) {
    FilterMap map = FilterMap.getInstance();
    FilterConfig config = null;
    if (!map.containsKey((Class)_callerClass)) {
      config = new FilterConfig(_callerClass);
      map.put(_callerClass, config);
    } else {
      config = map.get(_callerClass);
    }


    Set<String> actions = new HashSet<String>();
    for (String _a : _actions) {
      actions.add(_a);
    }
    if (actions.size() != _actions.length) {
      throw new RuntimeException(
        "duplicate before_action actions in controller" + _callerClass
        + ", :: " + Arrays.toString(_actions)
      );
    }

    Set<Method> availableMethods = new HashSet<Method>();
    Set<String> availableMethodStrs = new HashSet<String>();

    for (Method avail : _callerClass.getMethods()) {
      if (!avail.getDeclaringClass().equals(_callerClass)) {
        continue;
      }
      availableMethods.add(avail);
      availableMethodStrs.add(avail.getName());
    }


    if (_only) {
      for (String actionstr : _actions) {
        boolean found = false;
        for (Method m : availableMethods) {
          if (actionstr.equals(m.getName())) {
            found = true;
            config.addBefore(_filter, m);
          }
        }
        if (!found) {
          throw new RuntimeException("failed to match filter '" + _filter.toString()
            + "' to action '" + actionstr + "' in controller " + _callerClass);
        }
      }
    } else { // except
      for (Method m : availableMethods) {
        if (_actions.length == 0) {
          config.addBefore(_filter, m);
          continue;
        }

        for (String action : _actions) {
          if (!availableMethodStrs.contains(action)) {
            throw new RuntimeException("failed to find action '" + action
              + "' in before_action call in controller " + _callerClass
              + ". Did you typo the name?");
          }
          if (action.equals(m.getName())) {
          } else {
            config.addBefore(_filter, m);
            break;
          }
        }
      }
    }
  }




  public static void after_action(String _filter) {
    after_action_string(getCallerClass(), _filter, Except, new String[]{ });
  }

  public static void after_action(String _filter, boolean _only,
                                     String[] _actions) {
    after_action_string(getCallerClass(), _filter, _only, _actions);
  }

  private static void after_action_string(Class<?> _callerClass,
      String _filter, boolean _only, String[] _actions) {
    try {
      final Method filter_method
          = _callerClass.getMethod(_filter,
                                  ContainerRequestContext.class,
                                  ContainerResponseContext.class);

      AfterFilter filter = (creq,cres) -> {
        try {
          filter_method.invoke(null, creq, cres);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };

      after_action(_callerClass, filter, _only, _actions);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("failed to find filter method '" + _filter
                                 + "(ContainerRequestContext, "
                                 + "ContainerResponseContext)' in controller "
                                 + _callerClass);
    }
  }
  
  public static void after_action(Class<?> _class, String _method) {
    after_action_method(getCallerClass(), _class, _method, Except, new String[]{ });
  }

  public static void after_action(Class<?> _class, String _method,
                                      boolean _only, String[] _actions) {
    after_action_method(getCallerClass(), _class, _method, _only, _actions);
  }

  private static void after_action_method(Class<?> _callerClass,
      Class<?> _class, String _method, boolean _only, String[] _actions) {
    Method _filter = null;

    try {
      _filter = _class.getDeclaredMethod(
          _method,
          ContainerRequestContext.class,
          ContainerResponseContext.class
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    final Method flt = _filter;
    AfterFilter filter = (creq,cres) -> {
      try {
        flt.invoke(null, creq, cres);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };

    after_action(_callerClass, filter, _only, _actions);
  }


  public static
  void after_action(AfterFilter _filter) {
    after_action(getCallerClass(), _filter, Except, new String[]{ });    
  }

  public static
  void after_action(AfterFilter _filter,
      boolean _only, String[] _actions) {
    after_action(getCallerClass(), _filter, _only, _actions);
  }

  private static void after_action(Class<?> _callerClass,
      AfterFilter _filter,
      boolean _only, String[] _actions) {

    FilterMap map = FilterMap.getInstance();
    FilterConfig config = null;
    if (!map.containsKey((Class)_callerClass)) {
      config = new FilterConfig(_callerClass);
      map.put(_callerClass, config);
    } else {
      config = map.get(_callerClass);
    }

    Set<String> actions = new HashSet<String>();
    for (String _a : _actions) {
      actions.add(_a);
    }
    if (actions.size() != _actions.length) {
      throw new RuntimeException(
        "duplicate before_action actions in controller" + _callerClass
        + ", :: " + Arrays.toString(_actions)
      );
    }

    Set<Method> availableMethods = new HashSet<Method>();
    Set<String> availableMethodStrs = new HashSet<String>();
    for (Method avail : _callerClass.getMethods()) {
      availableMethods.add(avail);
      availableMethodStrs.add(avail.getName());
    }

    if (_only) {
      for (String actionstr : _actions) {
        boolean found = false;
        for (Method m : availableMethods) {
          if (actionstr.equals(m.getName())) {
            found = true;
            config.addAfter(_filter, m);
          }
        }
        if (!found) {
          throw new RuntimeException("failed to match filter '" + _filter.toString()
            + "' to action '" + actionstr + "' in controller " + _callerClass);
        }
      }
    } else { // except
      for (Method m : availableMethods) {
        if (_actions.length == 0) {
          config.addAfter(_filter, m);
          continue;
        }

        for (String action : _actions) {
          if (!availableMethodStrs.contains(action)) {
            throw new RuntimeException("failed to find action '" + action
              + "' in before_action call in controller " + _callerClass
              + ". Did you typo the name?");
          }
          if (action.equals(m.getName())) {
          } else {
            config.addAfter(_filter, m);
            break;
          }
        }
      }
    }
  }
}
