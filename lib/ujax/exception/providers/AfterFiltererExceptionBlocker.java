package lib.ujax.exception.providers;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import lib.ujax.filter.common.AfterFiltererException;

@Provider
public class AfterFiltererExceptionBlocker
implements ExceptionMapper<AfterFiltererException> {

  //terrible hack to circumvent the AfterFilterer being rerun on
  //Exception-blocker responses
  public Response toResponse(AfterFiltererException exception) {
    return Response.noContent().status(999).build();
  }

}