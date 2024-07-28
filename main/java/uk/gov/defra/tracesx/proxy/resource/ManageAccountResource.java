package uk.gov.defra.tracesx.proxy.resource;

import static uk.gov.defra.tracesx.proxy.enums.AuthType.B2C;
import static uk.gov.defra.tracesx.proxy.security.helpers.AuthTypeHelper.getGatewayHost;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.ACCESS_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.MANAGE_RETURN_URL_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.roles.Roles.ROLE_FOR_DECISION_ACCESS;
import static uk.gov.defra.tracesx.proxy.security.roles.Roles.ROLE_FOR_NOTIFICATION_ACCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.exceptions.DecodeException;
import uk.gov.defra.tracesx.proxy.exceptions.ResourceNotFoundException;
import uk.gov.defra.tracesx.proxy.security.filter.helpers.OauthLoginHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.AuthTypeHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.TokenHelper;
import uk.gov.defra.tracesx.proxy.security.roles.Roles;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class ManageAccountResource {

  private static final int MANAGE_RETURN_URL_COOKIE_LIFETIME = 86400;
  private static final String ENCODING = "UTF-8";
  private static final String RETURN_URL_FORMAT = "%s/%s/%s%s";
  private static final String LANDING_PAGE_URL_FORMAT = "%s/%s/%s/%s";
  private static final String PROTOCOL = "https://";
  private static final Pattern PROTOCOL_PATTERN = Pattern.compile("^https?\\:\\/\\/.*");
  private static final String MANAGE_RETURN_URL = "protected/manage/return";

  private CookieHelper cookieHelper;
  private String userManagementUri;
  private Roles roles;
  private AuthTypeHelper authTypeHelper;
  private ProxyConfiguration proxyConfiguration;
  private TokenHelper tokenHelper;
  private String schemeAndHost;
  private OauthLoginHelper oauthLoginHelper;
  
  @Autowired
  public ManageAccountResource(
      CookieHelper cookieHelper,
      Roles roles,
      AuthTypeHelper authTypeHelper,
      ProxyConfiguration proxyConfiguration,
      TokenHelper tokenHelper,
      OauthLoginHelper oauthLoginHelper,
      @Value("${external.userManagementUri}") String userManagementUri) {
    this.cookieHelper = cookieHelper;
    this.userManagementUri = userManagementUri;
    this.roles = roles;
    this.authTypeHelper = authTypeHelper;
    this.proxyConfiguration = proxyConfiguration;
    this.tokenHelper = tokenHelper;
    this.oauthLoginHelper = oauthLoginHelper;
    final String gatewayHost = proxyConfiguration.getGatewayHost();
    schemeAndHost = PROTOCOL_PATTERN.matcher(gatewayHost).matches()
        ? gatewayHost : PROTOCOL + gatewayHost;
  }

  @GetMapping("{type}/{env}/protected/**/manage")
  public RedirectView manageAccount(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable String type,
      @PathVariable String env,
      @RequestParam String returnUrl) throws UnsupportedEncodingException, MalformedURLException {
    RedirectView redirectView = new RedirectView();
    String host = request.getHeader(AuthTypeHelper.ORIGINAL_HOST_HEADER);

    // For B2C Selenium tests only, x-original-host cookie is set instead of a header
    host = getGatewayHost(host, request);

    String path = URLDecoder.decode(returnUrl, ENCODING);
    String uri = String.format(RETURN_URL_FORMAT, host, type, env, path);
    URL url = PROTOCOL_PATTERN.matcher(uri).matches()
        ? new URL(uri) : new URL(PROTOCOL + uri);
    String encodedUrl = URLEncoder.encode(url.toString(), ENCODING);
    cookieHelper.createCookie(MANAGE_RETURN_URL_COOKIE, encodedUrl,
        MANAGE_RETURN_URL_COOKIE_LIFETIME, response);
    redirectView.setUrl(userManagementUri);
    return redirectView;
  }

  @GetMapping(MANAGE_RETURN_URL)
  public RedirectView returnToFrontend(HttpServletRequest request, HttpServletResponse response)
      throws IOException, DecodeException  {
    RedirectView redirectView = new RedirectView();
    String path = cookieHelper.getCookie(MANAGE_RETURN_URL_COOKIE, request);
    if (path != null) {
      String url = URLDecoder.decode(path, ENCODING);
      cookieHelper.deleteCookie(MANAGE_RETURN_URL_COOKIE, request, response);
      redirectView.setUrl(url);
      return redirectView;
    } else if (authTypeHelper.getAuthTypeFromRequest(request).equals(B2C)) {
      String token = cookieHelper.getCookie(ACCESS_COOKIE, request);
      if (token == null) {
        final Optional<String> manageReturnUrl =
            Optional.of(schemeAndHost + "/" + MANAGE_RETURN_URL);
        redirectView.setUrl(oauthLoginHelper.createB2CLogin(manageReturnUrl, request, response));
        return redirectView;
      }
      if (tokenContainsRole(B2C, token, ROLE_FOR_DECISION_ACCESS)) {
        final String inspectorHomePageUrl = String.format(LANDING_PAGE_URL_FORMAT,
            schemeAndHost,
            proxyConfiguration.getDecisionUrlTemplate(),
            proxyConfiguration.getDeploymentEnvironment(),
            proxyConfiguration.getInspectorLandingUrl());
        redirectView.setUrl(inspectorHomePageUrl);
        return redirectView;
      } else if (tokenContainsRole(B2C, token, ROLE_FOR_NOTIFICATION_ACCESS)) {
        final String notifierHomePageUrl = String.format(LANDING_PAGE_URL_FORMAT,
            schemeAndHost,
            proxyConfiguration.getNotificationUrlTemplate(),
            proxyConfiguration.getDeploymentEnvironment(),
            proxyConfiguration.getImporterLandingUrl());
        redirectView.setUrl(notifierHomePageUrl);
        return redirectView;
      }
    }
    throw new ResourceNotFoundException();
  }
  
  private boolean tokenContainsRole(AuthType authType, String token, String role) 
      throws IOException, DecodeException {
    List<SimpleGrantedAuthority> simpleGrantedauthorities
        = roles.getUsersAuthorities(tokenHelper.getDecodedClaims(token, authType), authType);
    List<String> authorities =
        simpleGrantedauthorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
    return authorities.contains(role);
  }
}
