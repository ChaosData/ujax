package lib.ujax.filter.providers;

import javax.ws.rs.container.PreMatching;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Arrays;
import java.io.IOException;

import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class MethodOverrideFilter implements ContainerRequestFilter {

  @Context
  HttpServletRequest request;

  private static final HashSet<String> whitelist = new HashSet<String>(
    Arrays.asList("PUT", "PATCH", "DELETE")
  );

  @Override
  @SuppressWarnings("unchecked")
  public void filter(ContainerRequestContext crc) throws IOException {
    logger.debug(()->current());

    String method = crc.getMethod();
    if (method.equals("POST")) {
      String override = request.getParameter("_method");
      if (override != null && whitelist.contains(override)) {
        crc.setMethod(override);
      }
    }
  }

}
