package uk.gov.defra.tracesx.proxy.resource;

import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.AUTH_TYPE_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.LOGIN_RETURN_URL_COOKIE;
import static uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper.LOGIN_STATE_COOKIE;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.security.filter.helpers.OauthLoginHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;

import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class LogInResource {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogInResource.class);
  private static final String ENCODING = "UTF-8";

  private final OauthLoginHelper oauthLoginHelper;
  private final CookieHelper cookieHelper;

  @Autowired
  public LogInResource(OauthLoginHelper oauthLoginHelper, CookieHelper cookieHelper) {
    this.oauthLoginHelper = oauthLoginHelper;
    this.cookieHelper = cookieHelper;
  }

  @PostMapping
  public void login(
      String code,
      String state,
      HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {

    String originalState = cookieHelper.getCookie(LOGIN_STATE_COOKIE, request);
    String authType = cookieHelper.getCookie(AUTH_TYPE_COOKIE, request);
    String redirectUrlUrlEncoded = cookieHelper.getCookie(LOGIN_RETURN_URL_COOKIE, request);

    cookieHelper.removeCookiesUsedForLogin(request, response);

    boolean loginSuccess =
        oauthLoginHelper.loginUser(
            code,
            state,
            originalState,
            response,
            AuthType.valueOf(authType));

    String redirectUrl = URLDecoder.decode(redirectUrlUrlEncoded, ENCODING);
    LOGGER.info("Login succeded: {}", loginSuccess);
    if (!loginSuccess) {
      cookieHelper.deleteAllCookies(request, response);
    }

    response.sendRedirect(redirectUrl);
  }
}
