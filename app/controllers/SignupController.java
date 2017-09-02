package app.controllers;

import javax.servlet.http.*;
import javax.ws.rs.core.*;
import javax.ws.rs.container.*;
import javax.ws.rs.*;

import java.util.*;
import java.io.*;

import app.lib.com.example.SomeFilters;
import static lib.ujax.filter.security.CsrfFilter.protect_from_csrf;
import static lib.ujax.filter.security.AuthenticationFilter.require_login;
import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

import lib.ujax.models.Orm;
import org.hibernate.Session;
import org.hibernate.Query;
import app.models.User;

@Path("/signup")
public class SignupController extends ApplicationController {

  static {
    protect_from_csrf();
  }

  @GET
  public Response view(@Context HttpServletRequest req) {
    HashMap<String,Object> ctx = new HashMap<>();

    Map<String,Object> session = getSession(req);

    Object o_has_errors = session.get("has_errors");
    if (o_has_errors != null && (boolean)o_has_errors) {
      Object o_error = session.get("error");

      String error = null;
      if (o_error instanceof String) {
        error = (String)session.get("error");
      }

      if (error != null) {
        ctx.put("error", error);
        ctx.put("has_errors", true);

        session.remove("error");
        session.remove("has_errors");
      }
    }
    return ok(ctx, null, "text/html");
  }

  @POST
  public Response submit(@Context HttpServletRequest req,
                         @FormParam("username") String username,
                         @FormParam("password") String password) {
    Map<String,Object> session = getSession(req);

    //TODO validate password
    boolean password_ok = true;
    if (!password_ok) {
      session.put("error", "Password too weak. "
                           + "Password must contain at least 10 characters and "
                           + "contain at least characters from three of the "
                           + "following groups: uppercase, lowercase, numbers, "
                           + "and specials.");
      session.put("has_errors", true);
      return Response.noContent()
                     .status(Response.Status.SEE_OTHER)
                     .header("Location", "/signup")
                     .build();      
    }

    Session conn = Orm.get();
    conn.beginTransaction();

    Query query = conn.createQuery("from User as u where u.username=?");
    query.setString(0, username);
    List l = query.list();

    if (l.size() != 0) {
      session.put("error", "Account already exists.");
      session.put("has_errors", true);
      return Response.noContent()
                     .status(Response.Status.SEE_OTHER)
                     .header("Location", "/signup")
                     .build();
    }

    User u = new User();
    u.setUsername(username);
    u.setPassword(password);

    //TODO: 2FA

    conn.save(u);

    conn.getTransaction().commit();
    conn.close();

    session.put("username", username);
    session.put("user_id", Long.toString(u.getId()));

    return Response.noContent()
                    .status(Response.Status.SEE_OTHER)
                    .header("Location", "/todo/" + username)
                    .build();
  }

}
