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
import app.models.Todo;
import java.sql.Timestamp;

@Path("/todo")
public class TodoController extends ApplicationController {


  static {
    require_login();
    protect_from_csrf();

/*
    before_action("beforeOnlyFilter", Only, new String[]{"bar"} );
    before_action("beforeExceptFilter", Except, new String[]{"bar"} );

    after_action("afterOnlyFilter", Only, new String[]{"baz"} );
    after_action("afterExceptFilter", Only, new String[]{"baz"} );


    after_action(
      (creq,cres)->{
        System.out.println("Lambdas are fun.");
      }, Only, new String[]{"bar"}
    );

    before_action(SomeFilters.class, "BeforeFilter", Except, new String[]{"bar"});
    after_action(SomeFilters.class, "AfterFilter", Except, new String[]{"bar", "baz"});
  */
  }

  @Path("/learn.json")
  @GET
  public Response learn() {
    return Response.ok("{}", "application/json").build();
  }

  @GET
  public Response show(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());

    Map<String, Object> session = getSession(req);

    Object o_username = session.get("username");
    String username = null;
    if (o_username != null && o_username instanceof String) {
      username = (String)o_username;
    } else {
      //TODO: handle error
    }

    Object o_user_id = session.get("user_id");
    long user_id = -1;
    if (o_user_id != null && o_user_id instanceof String) {
      user_id = Long.parseLong((String)o_user_id);
    } else {
      //TODO: handle error
    }

    Session conn = Orm.get();
    conn.beginTransaction();

    Query query = conn.createQuery("from Todo as t where t.user.id=?");
    query.setLong(0, user_id);
    List l = query.list();

    for (Object o : l) {
      if (o instanceof Todo) {
        Todo t = (Todo)o;
        System.out.println(t.getText());
      } else {
        //TODO: handle error
      }
    }

    conn.getTransaction().commit();
    conn.close();

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", username);

    return ok(ctx, null, "text/html");
  }


  // POST /todo
  // { "id": 1234, "title": "<foo>", "complete": true }
  // callback on response, add server_id to localstorage

  @POST
  public Response create(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());



    Map<String, Object> session = getSession(req);

    return Response.ok().status(200).build();
  }

  // PATCH /todo/<server_id>
  // { "id":1234, "title": "<foo>"}
  // { "id":1234, "completed": true }



  // GET /todo/local/1234
  // callback on response to (if exists):
    // PATCH /todo/<server_id>
    // { "id":1234, "title": "<foo>"}
    // { "id":1234, "completed": true }
  //else:
    // POST /todo
    // { "id": 1234, "title": "<foo>", "complete": true }
    // callback on response, add server_id to localstorage

//DELETE /todo/<server_id>
//if 404 on response:
  //GET /todo/local/1234
  // callback on response to (if exists):
    // DELETE /todo/<server_id>
  //else:
    //do nothing


  @Path("/insert")
  @GET
  public Response insert(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());


    Todo tt = Orm.deserializer.fromJson("{\"id\":20, \"text\":\"foobar\"}", Todo.class);
    System.out.println(tt.getText());

    Map<String, Object> session = getSession(req);

    Object o_user_id = session.get("user_id");
    long user_id = -1;
    if (o_user_id != null && o_user_id instanceof String) {
      user_id = Long.parseLong((String)o_user_id);
    } else {
      //TODO: handle error
    }

    Session conn = Orm.get();
    conn.beginTransaction();

    Todo t = new Todo();
    
    t.setText("this is a test");
    t.setUser(conn.load(User.class, user_id));
    t.setCreation(new Timestamp(System.currentTimeMillis()));

    conn.save(t);

    conn.getTransaction().commit();
    conn.close();

    return Response.ok().status(200).build();
  }

}
