package lib.ujax.filter.security;

import java.util.HashSet;
import java.util.Arrays;
import lib.ujax.controllers.ApplicationControllerBase;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.ws.rs.container.ContainerRequestContext;

import java.security.SecureRandom;
import java.util.Base64;
import javax.ws.rs.WebApplicationException;
import static lib.ujax.extend.status.UnprocessableEntity.UNPROCESSABLE_ENTITY;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;

import static lib.ujax.common.Logging.logger;
import static lib.ujax.common.Logging.current;

public class AuthenticationFilter {

  public static final Response login_page =
    Response.noContent()
            .status(Response.Status.TEMPORARY_REDIRECT)
            .header("Location", "/login")
            .build();

  @SuppressWarnings("unchecked")
  public static void require_login() {

    ApplicationControllerBase.before_action((crc) -> {
      logger.debug(()->current());

      Map<String,Object> session =
        ApplicationControllerBase.getSession(crc, false);

      if (session == null) {
        throw new WebApplicationException(login_page);
      }



      Object o_user_id = session.get("user_id");
      if (o_user_id == null || !(o_user_id instanceof String)) {
        throw new WebApplicationException(login_page);
      }

    });
  }

}

