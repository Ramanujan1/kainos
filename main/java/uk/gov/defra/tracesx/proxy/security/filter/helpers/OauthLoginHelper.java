package uk.gov.defra.tracesx.proxy.security.filter.helpers;

import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.ACCESS_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.AUTH_TYPE_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.LOGIN_NONCE_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.LOGIN_RETURN_URL_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.LOGIN_STATE_COOKIE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.exceptions.DecodeException;
import uk.gov.defra.tracesx.proxy.security.helpers.AuthTypeHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.TokenHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OauthLoginHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(OauthLoginHelper.class);
  private static final String CHARSET = "UTF-8";

  private final OauthHelper oauthHelper;
  private final ProxyConfiguration proxyConfiguration;
  private final AuthTypeHelper authTypeHelper;
  private final CookieHelper cookieHelper;
  private final TokenHelper tokenHelper;
  private static final int LOGIN_COOKIE_LIFESPAN_IN_SECONDS = 3600;

  @Autowired
  public OauthLoginHelper(
      OauthHelper oauthHelper,
      ProxyConfiguration proxyConfiguration,
      AuthTypeHelper authTypeHelper,
      CookieHelper cookieHelper,
      TokenHelper tokenHelper) {
    this.oauthHelper = oauthHelper;
    this.proxyConfiguration = proxyConfiguration;
    this.authTypeHelper = authTypeHelper;
    this.cookieHelper = cookieHelper;
    this.tokenHelper = tokenHelper;
  }

  public boolean loginUser(
      String code,
      String state,
      String originalState,
      HttpServletResponse response,
      AuthType authType) throws DecodeException {

    boolean valid = oauthHelper.validResponseFromAuthProvider(state, originalState);

    if (valid) {
      String proxyAccessToken = oauthHelper.requestAndCreateAccessToken(code, authType);
      if (proxyAccessToken == null) {
        valid = false;
      } else {
        cookieHelper.createCookie(
            ACCESS_COOKIE,
            proxyAccessToken,
            tokenHelper.getSecondsTokenStillValidFor(proxyAccessToken, authType),
            response);
      }
    }
    return valid;
  }

  public void redirectToLogin(
      Optional<String> returnUrl, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    AuthType authType = authTypeHelper.getAuthTypeFromRequest(request);

    LOGGER.info("Logging in with authentication type {}", authType);

    String authUrl;
    if (authType.equals(AuthType.B2C)) {
      authUrl = createB2CLogin(returnUrl, request, response);
    } else {
      authUrl = createB2BLogin(returnUrl, request, response);
    }

    LOGGER.debug("Redirecting to {}", authUrl);
    response.sendRedirect(authUrl);
  }

  private String createB2BLogin(
      Optional<String> returnUrl,
      HttpServletRequest request,
      HttpServletResponse response)
      throws UnsupportedEncodingException {
    final String scope = URLEncoder.encode("openid profile", CHARSET);
    final String resource = URLEncoder.encode(proxyConfiguration.getClientId(), CHARSET);
    final String state = createState(request);
    final long nowInSeconds = (Instant.now().toEpochMilli() / 1000L);
    final String authUrl
        = String.format(
        "%s?client_id=%s&response_type=id_token+code&redirect_uri=%s&response_mode=form_post"
            + "&resource=%s&state=%s&scope=%s&nonce=%s",
        proxyConfiguration.getUserAuthorizationUri(),
        proxyConfiguration.getClientId(),
        URLEncoder.encode(proxyConfiguration.getProxyExternalUrlB2b(), CHARSET),
        resource,
        state,
        scope,
        nowInSeconds);

    createCookies(returnUrl, request, response, nowInSeconds, state, AuthType.B2B,
        proxyConfiguration.getProxyExternalUrlB2b());

    return authUrl;
  }

  public String createB2CLogin(
      Optional<String> returnUrl,
      HttpServletRequest request,
      HttpServletResponse response)
      throws UnsupportedEncodingException {
    final String scope
        = URLEncoder.encode(proxyConfiguration.getClientIdExternal() + " offline_access", CHARSET);
    final String state = createState(request);
    final long nowInSeconds = (Instant.now().toEpochMilli() / 1000L);
    final String authUrl
        = String.format(
        "%s?client_id=%s&response_type=code&redirect_uri=%s&response_mode=form_post"
            + "&state=%s&scope=%s&policyName=%s&journey=%s&serviceId=%s&prompt=login",
        proxyConfiguration.getUserAuthorizationUriExternal(),
        proxyConfiguration.getClientIdExternal(),
        URLEncoder.encode(proxyConfiguration.getProxyExternalUrlB2c(), CHARSET),
        state,
        scope,
        proxyConfiguration.getPolicyIdExternal(),
        proxyConfiguration.getJourneyExternal(),
        proxyConfiguration.getIdentityServiceIdExternal());
    
    createCookies(returnUrl, request, response, nowInSeconds, state, AuthType.B2C,
        proxyConfiguration.getProxyExternalUrlB2c());
    
    return authUrl;
  }
  
  private String createState(HttpServletRequest request) throws UnsupportedEncodingException {
    return UUID.randomUUID().toString() + "_" + URLEncoder.encode(request.getRequestURI(), CHARSET);
  }
  
  private void createCookies(Optional<String> returnUrl,
      HttpServletRequest request,
      HttpServletResponse response,
      long nowInSeconds,
      String state,
      AuthType authType,
      String proxyExternalUrl) {
    if (returnUrl.isPresent()) {
      cookieHelper.createCookie(LOGIN_RETURN_URL_COOKIE, returnUrl.get(),
          LOGIN_COOKIE_LIFESPAN_IN_SECONDS, response);
    } else {
      String requestUrl = proxyExternalUrl + request.getRequestURI();
      if (request.getQueryString() != null && request.getQueryString().length() > 0) {
        requestUrl = String.format("%s?%s", requestUrl, request.getQueryString());
      }
      
      cookieHelper.createCookie(LOGIN_RETURN_URL_COOKIE,
          String.format("%s", requestUrl),
          LOGIN_COOKIE_LIFESPAN_IN_SECONDS,
          response);
    }
    cookieHelper.createCookie(
        LOGIN_NONCE_COOKIE, String.valueOf(nowInSeconds), LOGIN_COOKIE_LIFESPAN_IN_SECONDS,
        response);
    cookieHelper.createCookie(
        AUTH_TYPE_COOKIE, authType.toString(), LOGIN_COOKIE_LIFESPAN_IN_SECONDS,
        response);
    cookieHelper.createCookie(
        LOGIN_STATE_COOKIE, state, LOGIN_COOKIE_LIFESPAN_IN_SECONDS,
        response);
  }
}
