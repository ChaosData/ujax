package lib.ujax.filter.providers;

import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;

public class SecurityHeadersResponseFilter implements ContainerResponseFilter {

  public static final String HSTS = "max-age=" + (60*60*24*30) + "; includeSubDomains";
  public static final String XFO = "DENY";
  public static final String XCTO = "nosniff";
  public static final String XPCDP = "none"; //fuck flash

  public static final String CSP = initCSP();

  private static String initCSP() {
    try {
      return
        Resources.toString(
          Resources.getResource("csp.policy"), Charsets.UTF_8
        ).trim();
    } catch (IOException e) {
      return
        "default-src 'none';"
        + " script-src 'self';"
        + " connect-src 'self';"
        + " img-src 'self' data:;"
        + " style-src 'self';"
        + " font-src 'self';"
        + " report-uri /cspfailure";
    }
  }


  @Override
  @SuppressWarnings("unchecked")
  public void filter(ContainerRequestContext creq,
                     ContainerResponseContext cres) throws IOException {
    cres.getHeaders().putSingle("Strict-Transport-Security", HSTS);
    cres.getHeaders().putSingle("X-Frame-Options", XFO);
    cres.getHeaders().putSingle("X-Content-Type-Options", XCTO);
    cres.getHeaders().putSingle("X-Permitted-Cross-Domain-Policies", XPCDP);
    cres.getHeaders().putSingle("Content-Security-Policy", CSP);
  }

}
