package lib.ujax.filter.common;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

public class FilterConfig {

  Class class_;

  public class FilterPair {
    public List<BeforeFilter> before_filters = new ArrayList<BeforeFilter>();
    
    public List<AfterFilter> after_filters = new ArrayList<AfterFilter>();
  }

  public Map<Method, FilterPair> mapping = new HashMap<Method, FilterPair>();


  public FilterConfig(Class _class) {
    class_ = _class;
  }

  @SuppressWarnings("unchecked")
  public void addBefore(BeforeFilter _filter, Method _action) {
    if (!_action.getDeclaringClass().equals(class_)) {
      logger.debug(()->current());
      logger.debug("self = '{}', declaring class = '{}'", 
                   ()->class_.getSimpleName(),
                   ()->_action.getDeclaringClass().getSimpleName());
      return;
    }

    FilterPair fp = null;
    if (!mapping.containsKey(_action)) {
      fp = new FilterPair();
      mapping.put(_action, fp);
    } else {
      fp = mapping.get(_action);
    }
    fp.before_filters.add(_filter);
  }

  @SuppressWarnings("unchecked")
  public void addAfter(AfterFilter _filter, Method _action) {
    if (!_action.getDeclaringClass().equals(class_)) {
      logger.debug(()->current());
      logger.debug("self = '{}', declaring class = '{}'", 
                   ()->class_.getSimpleName(),
                   ()->_action.getDeclaringClass().getSimpleName());
      return;
    }

    FilterPair fp = null;
    if (!mapping.containsKey(_action)) {
      fp = new FilterPair();
      mapping.put(_action, fp);
    } else {
      fp = mapping.get(_action);
    }
    fp.after_filters.add(_filter);
  }

}