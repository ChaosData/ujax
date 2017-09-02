package lib.ujax.exception.providers;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

@Provider
public class ExceptionBlocker implements ExceptionMapper<Exception> {

  public Response toResponse(Exception e) {
    //hack b/c JAX-RS is dumb and WebApplicationException Responses without
    //an entity/body don't get handled in the normal flow
    if (e instanceof WebApplicationException) {
      Response r = ((WebApplicationException)e).getResponse();
      if (r.getEntity() == null) {
        return r;
      }
    }
    e.printStackTrace();
    return Response.noContent().status(500).build();
  }

}
