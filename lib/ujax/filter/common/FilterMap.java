package lib.ujax.filter.common;

import java.util.*;

public class FilterMap {
  //note: we want to keep this alive so that filters can be added after init
    
  private static FilterMap ref = new FilterMap();

  private Map<Class, FilterConfig> mapping = null;

  private FilterMap() {
    mapping = new HashMap<Class, FilterConfig>();
  }

  public static synchronized FilterMap getInstance() {
    if (ref == null) {
      ref = new FilterMap();
    }
    return ref;
  }

  public synchronized boolean containsKey(Class _controller) {
    return mapping.containsKey(_controller);
  }

  public synchronized FilterConfig get(Class _controller) {
    return mapping.get(_controller);
  }


  public synchronized void put(Class _controller, FilterConfig _filterConfig) {
    mapping.put(_controller, _filterConfig);
  }




}