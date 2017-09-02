package lib.ujax.models;

import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import com.google.common.reflect.ClassPath;
import javax.persistence.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static lib.ujax.common.Logging.logger;

public class Orm {

  //public static final Gson serializer = Serializer.getInstance();
  //public static final Gson deserializer = Deserializer.getInstance();

  public static final Gson serializer = new GsonBuilder()
      .setExclusionStrategies(new AttrReadableFilter())
      .create();;
  
  public static final Gson deserializer = new GsonBuilder()
      .setExclusionStrategies(new AttrAccessibleFilter())
      .create();

  private static final SessionFactory sessionFactory;
  static {
    try {
      Properties prop= new Properties();
      prop.setProperty("hibernate.connection.url", "jdbc:h2:./db/todomvc");
      prop.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
      prop.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      prop.setProperty("hibernate.hbm2ddl.auto", "update");
      prop.setProperty("hibernate.show_sql", "false");

      prop.setProperty(
        "hibernate.archive.scanner",
        "org.hibernate.boot.archive.scan.internal.StandardScanner"
      );
      prop.setProperty("hibernate.archive.autodetection", "class");

      //prop.setProperty("", "");

      StandardServiceRegistryBuilder builder =
          new StandardServiceRegistryBuilder()
          .applySettings(prop);

      Configuration configuration = new Configuration();
      configuration.addProperties(prop);


      logger.debug("Start model search.");
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try {
        for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
          String name = info.getName();
          if (name.startsWith("app.models.")) {
            final Class<?> model = info.load();
            if ( model.getAnnotation(Entity.class) != null ) {
              logger.debug("Found model: " + model);

              // force class initialization to run static { code }
              Object c = model.getDeclaredConstructor().newInstance();

              configuration.addAnnotatedClass(model);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      logger.debug("End model search.");



      sessionFactory = configuration.buildSessionFactory(builder.build());
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }
  public static Session get()
      throws HibernateException {
    return sessionFactory.openSession();
  }
  
/*
  public static void main(String... args){
    Session session=getSession();
    session.beginTransaction();
    User user=(User)session.get(User.class, new Integer(1));
    System.out.println(user.getName());
    session.close();
  }
*/
}
