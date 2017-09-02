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

@Path("/login")
public class LoginController extends ApplicationController {

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

    Session conn = Orm.get();
    conn.beginTransaction();

    Query query = conn.createQuery("from User as u where u.username=?");
    query.setString(0, username);
    List l = query.list();

    User u = null;
    if (l.size() != 0) {
      Object o = l.get(0);
      if (o instanceof User) {
        u = (User)o;
      }
    }

    if (u == null) {
      u = new User();
      u.setPassword("bad_password");
      boolean not_valid = u.validatePassword(password);
      if (not_valid) {
        logger.error("bad_password actually guessed, heh.");
      }
      session.put("error", "Invalid credentials.");
      session.put("has_errors", true);

      return Response.noContent()
                     .status(Response.Status.SEE_OTHER)
                     .header("Location", "/login")
                     .build();
    }

    boolean valid = u.validatePassword(password);

    if (!valid) {
      session.put("error", "Invalid credentials.");
      session.put("has_errors", true);

      return Response.noContent()
                     .status(Response.Status.SEE_OTHER)
                     .header("Location", "/login")
                     .build();
    }

    //TODO: 2FA

    conn.getTransaction().commit();
    conn.close();

    session.put("username", u.getUsername());
    session.put("user_id", Long.toString(u.getId()));

    return Response.noContent()
                    .status(Response.Status.SEE_OTHER)
                    .header("Location", "/todo")
                    .build();
  }

}
