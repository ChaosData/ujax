package app.lib.com.example;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

public class SomeFilters {

  public static void BeforeFilter(ContainerRequestContext crc) {
    logger.debug(()->current());
  }

  public static void AfterFilter(ContainerRequestContext creq,
                                 ContainerResponseContext cres) {
    logger.debug(()->current());
  }
}