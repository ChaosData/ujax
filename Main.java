import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Enumeration;
import com.google.common.reflect.ClassPath;

import lib.ujax.filter.dyn.ControllerFilterFeature;
import static lib.ujax.common.Logging.logger;

/*
import org.eclipse.jetty.server.Server;
import java.net.InetSocketAddress;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
*/

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.server.handlers.resource.FileResourceManager;
import java.io.File;


class Main {

  public static final int PORT = 8000;
  public static final String BIND = "127.0.0.1";

  public static final Class<? extends Servlet> servletClass
      = org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class;

  public static void main(String[] args) {

    /*
    Server server = new Server(new InetSocketAddress(BIND, PORT));
    ServletContextHandler context = new ServletContextHandler(
      ServletContextHandler.NO_SESSIONS
    );
    context.setContextPath("/");
    server.setHandler(context);

    ServletHolder appServlet = context.addServlet(servletClass, "/*");
    appServlet.setInitOrder(1);
    appServlet.setInitParameter("javax.ws.rs.Application", "Main$App");
    
    ServletHolder staticServlet = context.addServlet(DefaultServlet.class,"/static/*");
    staticServlet.setInitParameter("resourceBase","./static");
    staticServlet.setInitParameter("pathInfoOnly","true");

    try {
      server.start();
      server.join();
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }
    */

    
    DeploymentInfo servletBuilder = new DeploymentInfo().setDeploymentName("")
      .setClassLoader(Main.class.getClassLoader())
      .setContextPath("/")
      .setResourceManager(new FileResourceManager(new File("static/"), 0))
      .addServlets(
        Servlets.servlet(servletClass).addMapping("/*")
          .addInitParam("javax.ws.rs.Application", "Main$App"),
        Servlets.servlet(DefaultServlet.class).addMapping("/static/*")
          .addMapping("/favicon.ico")
          .addInitParam("resolve-against-context-root", "false")
          .addInitParam("default-allowed", "true")
    );

    DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
    manager.deploy();

    try {
      Undertow.builder()
        .addHttpListener(PORT, BIND)
        .setHandler(manager.start())
        .build().start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    

  }

  @ApplicationPath("/")
  public static class App extends Application {
    public App() {
      super();
    }

    @Path("/")
    public static class Base {

      @Path("/{path: .*}")
      @GET
      public Response fallback(@Context HttpServletRequest req, @Context UriInfo ui) {
        logger.debug("fallback handler called");
        logger.debug("--");
        logger.debug("getPathInfo: " + req.getPathInfo());
        logger.debug("getRequestURI: " + req.getRequestURI());
        ui.getPathSegments().forEach((ps) -> {
          logger.debug(ps.getPath());
        });
        logger.debug("--");
        ui.getPathParameters().forEach((k,v) -> {
          logger.debug(k + " :: " + v);
          for(String s : v) {
            logger.debug("value: " + s);
          }
        });
        logger.debug("--");
        req.getParameterMap().forEach((k,v) -> {
          logger.debug(k + " :: " + Arrays.toString(v));
        });
        try {
        Enumeration<String> hns = req.getHeaderNames();
		if (hns.hasMoreElements()) {
			for (String hn = hns.nextElement(); hns.hasMoreElements(); hn = hns.nextElement()) {
				logger.debug(hn);
			}
		  logger.debug("=====");
		}
		hns = req.getHeaders("X-Forwarded-For");
		if (hns.hasMoreElements()) {
			logger.debug(">>" + hns.nextElement() + "<<");
			if (hns.hasMoreElements()) {
				logger.debug("]" + hns.nextElement() + "[");
			}
			logger.debug("=====");
		}
        } catch (Throwable e) {
          e.printStackTrace();
        }
        logger.debug("=====");
        return Response.ok().build();
      }
    }

    @Override
    public Set<Class<?>> getClasses() {
      Set<Class<?>> s = new HashSet<Class<?>>();
      s.add(Base.class);

      logger.debug("Start controller search");
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try {
        for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
          String name = info.getName();
          if (name.startsWith("app.controllers.")) {
            final Class<?> controller = info.load();
            if ( controller.getAnnotation(Path.class) != null ) {
              logger.debug("Found controller: " + controller);

              // force class initialization to run static { code }
              Object c = controller.getDeclaredConstructor().newInstance();

              s.add(controller);
            }
          } else if (name.startsWith("lib.ujax.filter.providers")) {
            final Class<?> provider = info.load();
            s.add(provider);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      logger.debug("End controller search");

      s.add(ControllerFilterFeature.class);
      return s;
    }
  }
}
