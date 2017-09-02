package lib.ujax.filter.providers;

import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;


import javax.servlet.ServletException;
import java.util.Map;
import javax.ws.rs.core.Cookie;

import lib.ujax.filter.common.JWTSecret;

@Priority(Priorities.AUTHENTICATION)
public class JWTRequestFilter implements ContainerRequestFilter {
  
  @Override
  public void filter(ContainerRequestContext crc) throws IOException {
    Cookie session_cookie = crc.getCookies().get("session");
    if (session_cookie == null) {
      return;
    }

    String session = session_cookie.getValue();
    Map<String, Object> decoded = null;
    try {
      decoded = JWTSecret.verifier.verify(session);
    } catch (Exception e) {
      throw new RuntimeException("Invalid session.");
    }

    long now = System.currentTimeMillis()/1000;
    Long expires = 0L;
    try {
      expires = Long.parseLong((String)decoded.get("expires"));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }

    if (now < expires.longValue()) {
      crc.setProperty("session", decoded);
    }
    

  }

}
