package lib.ujax.filter.controllers;

import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.util.function.*;

import java.lang.reflect.*;
import java.util.*;

import lib.ujax.filter.common.BeforeFilter;

public class BeforeFilterer implements ContainerRequestFilter {

  List<BeforeFilter> filters = null;
  HttpServletRequest req = null;

  public BeforeFilterer(List<BeforeFilter> _filters, HttpServletRequest _req) {
    filters = _filters;
    req = _req;
  }

  @Override
  public void filter(ContainerRequestContext requestContext)
      throws WebApplicationException {
    if (filters == null) {
      return;
    }

    requestContext.setProperty("request", req);

    for (BeforeFilter f : filters) {
      try {
        f.accept(requestContext);
      } catch (WebApplicationException w) {
        throw w;
      } catch (RuntimeException re) {
        Throwable t = re.getCause();
        if (t instanceof InvocationTargetException) {
          Throwable t2 = t.getCause();
          if (t2 instanceof WebApplicationException) {
            throw (WebApplicationException)t2;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new WebApplicationException(Response.noContent().status(500).build());
      }
    }
  }

}