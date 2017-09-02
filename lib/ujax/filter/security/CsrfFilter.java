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

public class CsrfFilter {

  private static final HashSet<String> whitelist = new HashSet<String>(
    Arrays.asList("GET", "HEAD", "OPTIONS")
  );

  static boolean isEqual(byte[] a, byte[] b) {
    if (a.length != b.length) {
        return false;
    }

    int result = 0;
    for (int i = 0; i < a.length; i++) {
      result |= a[i] ^ b[i];
    }
    return result == 0;
  }


  @SuppressWarnings("unchecked")
  public static void protect_from_csrf() {

    ApplicationControllerBase.before_action(

      (crc) -> {
        logger.debug(()->current());

        Map<String,Object> session =
          ApplicationControllerBase.getSession(crc, false);

        String csrf_token = null;
        if (session == null || whitelist.contains(crc.getMethod())) {
          if (session == null) {
            session = new HashMap<String,Object>();
            crc.setProperty("session", session);
          }
          csrf_token = (String)session.get("csrf_token");
          if (csrf_token == null) {
            byte[] rand = new byte[16];
            new SecureRandom().nextBytes(rand);
            csrf_token = Base64.getEncoder().encodeToString(rand);
            session.put("csrf_token", csrf_token);
          }
          if (!whitelist.contains(crc.getMethod())) {
            throw new WebApplicationException(
              Response.noContent().status(UNPROCESSABLE_ENTITY).build()
            );
          } else {
            return;
          }
        }

        csrf_token = (String)session.get("csrf_token");
        if (csrf_token == null) {
          //note: this would be strange
          throw new WebApplicationException(
            Response.noContent().status(UNPROCESSABLE_ENTITY).build()
          );
        }

        HttpServletRequest request =
            (HttpServletRequest)crc.getProperty("request");
        if (request == null) {
          throw new WebApplicationException(
            Response.noContent().status(UNPROCESSABLE_ENTITY).build()
          );
        }

        String suppliedToken[] = request.getParameterMap().get("csrf_token");
        if (suppliedToken == null || suppliedToken.length != 1) {
          suppliedToken = new String[1];
          suppliedToken[0] = crc.getHeaderString("x-csrf-token");
          if (suppliedToken[0] == null) {
            throw new WebApplicationException(
              Response.noContent().status(UNPROCESSABLE_ENTITY).build()
            );
          }
        }

        if (!isEqual(csrf_token.getBytes(), suppliedToken[0].getBytes())) {
          throw new WebApplicationException(
            Response.noContent().status(UNPROCESSABLE_ENTITY).build()
          );
        }

      }
    );
  }

}

