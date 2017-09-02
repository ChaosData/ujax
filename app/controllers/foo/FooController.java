package app.controllers.foo;

import app.controllers.*;
import javax.ws.rs.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.function.*;
import com.github.mustachejava.TemplateFunction;

import static lib.ujax.filter.controllers.CsrfFilter.protect_from_csrf;

import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

@Path("/foo/foo")
public class FooController extends app.controllers.FooController {
  
  static {
    protect_from_csrf();
  }

  @GET
  public Response index(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());
    return foo(req, ui);
  }



  @GET
  @Path("/{id}")
  public Response foo(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("title", "foofoo");
    ctx.put("name", ui.getPathParameters().getFirst("id"));
    ctx.put("text", req.getParameter("text"));
    ctx.put("func", (Object)(
      (Function<String,String>)(
        (s)->{
          return "Hey {{name}}, this is a callable (input pre-expanded)."
          + "<br>(" + s + ")";
        }
      )
    ));

    ctx.put("func2", (Object)(
      (TemplateFunction)(
        (s)->{
          if (s == null) {
            s = "null";
          }
          return "Hey {{name}}, this is a mustache lambda."
          + "(input is raw, but will be expanded if returned)."
          + "<br>raw input (escaping &#x7b;/&#x7d; to prevent rendering): "
          + s.replace("{", "&#x7b;").replace("}","&#x7d;")
          + "<br>rendered input: " + s;
        }
      )
    ));


    return ok(ctx, null, "text/html");
  }

}