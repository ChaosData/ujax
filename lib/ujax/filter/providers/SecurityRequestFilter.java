package lib.ujax.filter.providers;

import javax.ws.rs.container.PreMatching;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import java.io.IOException;

import java.io.BufferedReader;
import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class SecurityRequestFilter implements ContainerRequestFilter {
  
  @Context
  HttpServletRequest request;
  
  @Override
  public void filter(ContainerRequestContext crc) throws IOException {
    logger.debug(()->current());

    //CSP
    if (request.getRequestURI().equals("/cspfailure")
     && request.getMethod() == "POST") {
      StringBuffer sb = new StringBuffer();
      String line = null;
      try {
        BufferedReader reader = request.getReader();
      while ((line = reader.readLine()) != null)
        sb.append(line);
      } catch (Exception e) { }

      //TODO: stash these somewhere

      logger.debug("{}", () -> sb.toString());
      crc.abortWith(Response.ok().build());
      return;
    }

    //Flash, if by some "miracle" this file should end up accessible as such
    if (request.getRequestURI().endsWith("/crossdomain.xml")) {
      crc.abortWith(Response.serverError().status(404).build());
      return;
    }    

  }

}
