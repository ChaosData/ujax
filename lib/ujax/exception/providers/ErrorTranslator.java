package lib.ujax.exception.providers;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import javax.ws.rs.ClientErrorException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.core.MediaType;

@Provider
public class ErrorTranslator implements ExceptionMapper<ClientErrorException> {

  static private Map<String,String> pages = new ConcurrentHashMap<String,String>();

  private static String getPage(String status) {
    String html = pages.get(status);
    if (html == null) {
      try {
        html = new String(
          Files.readAllBytes(
            Paths.get("app/views/errors/" + status + ".html")
          )
        );
      } catch (Exception e) {
        html = "";
      }
      pages.put(status, html);
    }
    return html;
  }

  public Response toResponse(ClientErrorException e) {
    Response r = e.getResponse();

    int status = r.getStatus();
    if (status == 405) {
      status = 404;
    }
    String sstatus = Integer.toString(status);
    String html = getPage(sstatus);
    return Response.noContent()
                   .status(status)
                   .entity(html)
                   .type(new MediaType("text", "html", "utf-8"))
                   .build();
  }

}