package app.controllers;

import javax.servlet.http.*;
import javax.ws.rs.core.*;
import javax.ws.rs.container.*;
import javax.ws.rs.*;

import java.util.*;
import java.io.*;

import app.lib.com.example.SomeFilters;
import static lib.ujax.filter.controllers.CsrfFilter.protect_from_csrf;
import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

@Path("/foo")
public class FooController extends ApplicationController {

  static {
    protect_from_csrf();

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
  }

  public static void beforeOnlyFilter(ContainerRequestContext _crc) {
    logger.debug(()->current());
    throw new WebApplicationException(Response.ok("parent").build());
  }

  public static void beforeExceptFilter(ContainerRequestContext _crc) {
    logger.debug(()->current());

    HttpServletRequest hsr = (HttpServletRequest)_crc.getProperty("request");
    logger.debug("{}", hsr.getParameter("csrf_token"));
  }

  public static void afterOnlyFilter(ContainerRequestContext _creq,
                              ContainerResponseContext _cres) {
    logger.debug(()->current());
    _cres.setEntity("barbaz", null, new MediaType("text", "html", "utf-8"));
  }

  public static void afterExceptFilter(ContainerRequestContext _creq,
                              ContainerResponseContext _cres) {
    logger.debug(()->current());
  }


  @Path("/{id}")
  @GET
  public Response foo(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("title",
      "public Response foo(@Context HttpServletRequest req, @Context UriInfo ui)"
    );
    ctx.put("name", ui.getPathParameters().getFirst("id"));

    Map<String,Object> session = getSession(req);
    String test = (String)session.get("test");
    System.out.println(test);
    session.put("test", "hello world");

    return ok(ctx, null, "text/html");
  }

  @Path("/{id}")
  @PUT
  public Response fooput(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("title", "fooput");
    ctx.put("name", ui.getPathParameters().getFirst("id"));
    ctx.put("text", req.getParameter("text"));

    return ok(ctx, null, "text/html");
  }


  @Path("/bar")
  @GET
  public Response bar(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());
    return Response.ok().build();
  }

  @Path("/baz")
  @GET
  public Response baz(@Context HttpServletRequest req, @Context UriInfo ui) {
    logger.debug(()->current());
    return Response.ok().status(410).build();
  }

  @Path("/bar")
  @PUT
  public Response putbar(@Context HttpServletRequest req, @Context UriInfo ui, @FormParam("csrf_token") String token) {
    logger.debug(()->current());
    logger.debug("{}", ()->req.getParameter("csrf_token"));
    logger.debug("{}", token);

    return Response.ok("gg").build();
  }

}
