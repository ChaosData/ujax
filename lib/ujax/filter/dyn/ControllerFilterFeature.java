package lib.ujax.filter.dyn;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.DynamicFeature;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import java.lang.reflect.*;
import java.util.*;

import lib.ujax.filter.common.FilterMap;
import lib.ujax.filter.common.FilterConfig;
import lib.ujax.filter.controllers.BeforeFilterer;
import lib.ujax.filter.controllers.AfterFilterer;


@Priority(Priorities.USER)
public class ControllerFilterFeature implements DynamicFeature {
  
  @Context
  private HttpServletRequest requestProxy;

  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    Class<?> rc = resourceInfo.getResourceClass();
    Method rm = resourceInfo.getResourceMethod();

    FilterMap fm = FilterMap.getInstance();

    FilterConfig fc = fm.get(rc);
    if (fc == null) {
      return;
    }

    FilterConfig.FilterPair before_after = fc.mapping.get(rm);
    if (before_after == null) {
      return;
    }
    
    if (!before_after.before_filters.isEmpty()) {
      context.register(new BeforeFilterer(before_after.before_filters, requestProxy));
    }

    if (!before_after.after_filters.isEmpty()) {
      context.register(new AfterFilterer(before_after.after_filters));
    }
  }

}