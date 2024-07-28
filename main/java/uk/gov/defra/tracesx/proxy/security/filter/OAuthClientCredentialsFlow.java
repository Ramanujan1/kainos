package uk.gov.defra.tracesx.proxy.security.filter;

import static uk.gov.defra.tracesx.proxy.security.roles.Roles.ROLE_FOR_SOAPSEARCH_ACCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.security.jwt.JwtService;
import uk.gov.defra.tracesx.proxy.security.models.ClientCredentialsUserDetails;

import java.util.Collections;
import java.util.List;

@Component
public class OAuthClientCredentialsFlow {

  private final JwtService jwtService;
  private final ProxyConfiguration proxyConfiguration;

  @Autowired
  public OAuthClientCredentialsFlow(JwtService jwtService, ProxyConfiguration proxyConfiguration) {
    this.jwtService = jwtService;
    this.proxyConfiguration = proxyConfiguration;
  }

  UsernamePasswordAuthenticationToken getSpringAuth(String serviceNameForToken) {

    String accessTokenClientCredentials =
        jwtService.getProxyClientCredentialsAccessToken(serviceNameForToken);
    List<SimpleGrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority(ROLE_FOR_SOAPSEARCH_ACCESS));

    ClientCredentialsUserDetails user = new ClientCredentialsUserDetails(
        accessTokenClientCredentials, proxyConfiguration.getJwtAudience(), authorities);

    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
  }
}
