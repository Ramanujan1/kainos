package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.security.models.OauthUserDetails;

@Component
public class AccessTokenHelper {

  public String getAccessToken() {
    SecurityContext sc = SecurityContextHolder.getContext();
    if (sc != null) {
      OauthUserDetails userDetails =
          (OauthUserDetails) sc.getAuthentication().getPrincipal();

      return userDetails.getAccessToken();
    }
    return "";
  }
}
