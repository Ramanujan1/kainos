package uk.gov.defra.tracesx.proxy.security.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthLoginFilter extends AbstractAuthenticationProcessingFilter {

  private final OAuthServerCodeGrantFlow oauthServerCodeGrantFlow;
  private final OAuthClientCredentialsFlow oauthClientCredentialsFlow;

  private static final String SOAP_URI_PREFIX = "/soapsearch/";
  private static final String SOAP_NAME_FOR_TOKEN = "soap";

  public OAuthLoginFilter(
      String defaultFilterProcessesUrl,
      OAuthServerCodeGrantFlow oauthServerCodeGrantFlow,
      OAuthClientCredentialsFlow oauthClientCredentialsFlow) {
    super(defaultFilterProcessesUrl);
    this.oauthServerCodeGrantFlow = oauthServerCodeGrantFlow;
    this.oauthClientCredentialsFlow = oauthClientCredentialsFlow;
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  //No state, so this is run per request
  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    if (request.getRequestURI().toLowerCase().startsWith(SOAP_URI_PREFIX)) {
      return oauthClientCredentialsFlow.getSpringAuth(SOAP_NAME_FOR_TOKEN);
    } else {
      return oauthServerCodeGrantFlow.getSpringAuth(request, response);
    }
  }

  //Customized version of spring security filter, to remove session state calls and events
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (!requiresAuthentication(request, response)) {
      chain.doFilter(request, response);
      return;
    }

    Authentication authResult;

    try {
      authResult = attemptAuthentication(request, response);
      if (authResult == null) {
        return;
      }
    } catch (AuthenticationException failed) {
      unsuccessfulAuthentication(request, response, failed);
      return;
    }

    // Authentication success
    SecurityContextHolder.getContext().setAuthentication(authResult);

    chain.doFilter(request, response);
  }


  private static class NoopAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) {
      throw new UnsupportedOperationException("No authentication should be done with this "
          + "AuthenticationManager");
    }
  }
}


