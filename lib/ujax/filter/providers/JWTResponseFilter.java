package lib.ujax.filter.providers;

import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

import javax.servlet.ServletException;
import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import lib.ujax.filter.common.JWTSecret;
import lib.ujax.controllers.ApplicationControllerBase;

public class JWTResponseFilter implements ContainerResponseFilter {

  @Override
  @SuppressWarnings("unchecked")
  public void filter(ContainerRequestContext creq,
                     ContainerResponseContext cres) throws IOException {
    Map<String,Object> session = ApplicationControllerBase.getSession(creq, false);
    if (session == null) {
      return;
    }

    long expires = (System.currentTimeMillis()/1000) + JWTSecret.MAX_AGE;

    session.put("expires", Long.toString(expires));

    String signed = JWTSecret.signer.sign(session);

    Cookie cv = new Cookie("session", signed);
    NewCookie nc = new NewCookie(cv, null, JWTSecret.MAX_AGE, true);
    cres.getHeaders().putSingle("set-cookie", nc);

  }

}
