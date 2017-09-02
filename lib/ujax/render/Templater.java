package lib.ujax.render;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Writer;
import java.io.StringWriter;
import java.io.File;

import lib.ujax.controllers.ApplicationControllerBase;
import javax.ws.rs.WebApplicationException;

import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class Templater {

  private static final String PKG_PATH = "app.controllers.";

  private static MustacheFactory stache_factory = new DefaultMustacheFactory(
      new File("app/views/")
  );
  private static Map<String,Mustache> staches = new ConcurrentHashMap<String,Mustache>();

  public static Response ok(String template_path, Map<String, Object> ctx,
      Map<String,String> headers, String type) {
    return render(template_path, ctx, headers, type, 200);
  }

  public static Response ok(Map<String, Object> ctx,
      Map<String,String> headers, String type,
      ApplicationControllerBase controller) {

    String[] classAndMethod = controller.getCallerClassAndMethod();
    if (classAndMethod == null) {
      throw new WebApplicationException(
        Response.noContent().status(500).build()
      );
    }

    String className = classAndMethod[0];

    if (className.endsWith("Controller")) {
      className = className.
                  substring(
                    className.lastIndexOf(PKG_PATH) + PKG_PATH.length(),
                    className.length()-10
                  )
                  .replace('.', '/')
                  .toLowerCase();
    } else {
      className = className
                  .substring(className.lastIndexOf(".")+1)
                  .toLowerCase();
    }

    String methodName = classAndMethod[1].toLowerCase();
    String ext = type.substring(type.lastIndexOf("/")+1);

    return render(className + "/" + methodName + "." + ext,
                  ctx, headers, type, 200);
  }

  public static Response err(String template_path, Map<String, Object> ctx,
      Map<String,String> headers, String type, int status) {
    return render(template_path, ctx, headers, type, status);
  }


  public static Response err(Map<String, Object> ctx,
      Map<String,String> headers, String type, int status,
      ApplicationControllerBase controller) {
    String[] classAndMethod = controller.getCallerClassAndMethod();
    if (classAndMethod == null) {
      throw new WebApplicationException(
        Response.noContent().status(500).build()
      );
    }

    String className = classAndMethod[0];

    if (className.endsWith("Controller")) {
      className = className.
                  substring(
                    className.lastIndexOf(PKG_PATH) + PKG_PATH.length(),
                    className.length()-10
                  )
                  .replace('.', '/')
                  .toLowerCase();
    } else {
      className = className
                  .substring(className.lastIndexOf(".")+1)
                  .toLowerCase();
    }

    String methodName = classAndMethod[1].toLowerCase();

    String ext = type.substring(type.lastIndexOf("/")+1);

    return render(className + "/" + methodName + "." + ext,
                  ctx, headers, type, status);
  }

  private static Mustache getTemplate(String path) {
    Mustache template = staches.get(path);
    if (template == null) {
      template = stache_factory.compile(path);
    }
    return template;
  }


  public static Response render(String template_path, Map<String, Object> ctx,
      Map<String,String> headers, String type, int status) {

    try {
      Mustache template = getTemplate(template_path);

      Response.ResponseBuilder rb = Response.noContent();
      rb.status(status);

      if (headers != null) {
        headers.forEach((k,v)->{
          rb.header(k, v);
        });
      }

      Writer writer = new StringWriter();
      template.execute(writer, ctx);

      rb.entity(writer.toString());

      if (type == null) {
        rb.type("application/json");
      } else {
        rb.type(type);
      }
      return rb.build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.noContent().status(500).build();
    }

  }


}