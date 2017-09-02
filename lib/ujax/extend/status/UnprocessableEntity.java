package lib.ujax.extend.status;

import javax.ws.rs.core.Response;

public class UnprocessableEntity implements Response.StatusType {

 public static final UnprocessableEntity UNPROCESSABLE_ENTITY
      = new UnprocessableEntity();
  
  public Response.Status.Family getFamily() {
    return Response.Status.Family.CLIENT_ERROR;
  }

  public String getReasonPhrase() {
    return "Unprocessable Entity";
  }

  public int getStatusCode() {
    return 422;
  }
}
