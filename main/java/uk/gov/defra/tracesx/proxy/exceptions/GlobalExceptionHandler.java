package uk.gov.defra.tracesx.proxy.exceptions;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.security.helpers.AuthTypeHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.UrlHelper;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final AuthTypeHelper authTypeHelper;
  private final ProxyConfiguration proxyConfiguration;
  private final CookieHelper cookieHelper;
  public static final String ERROR_MSG = "error_msg";
  private static final org.slf4j.Logger LOG =
      LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Autowired
  public GlobalExceptionHandler(ProxyConfiguration proxyConfiguration, CookieHelper cookieHelper,
                                AuthTypeHelper authTypeHelper) {
    this.proxyConfiguration = proxyConfiguration;
    this.cookieHelper = cookieHelper;
    this.authTypeHelper = authTypeHelper;
  }

  @ExceptionHandler(value = {MultipleOrganisationsException.class})
  protected void handleMultipleOrgs(
      HttpServletRequest request,
      HttpServletResponse response,
      MultipleOrganisationsException ex) throws IOException {
    LOG.error("Multiple Organisations detected, user redirected to error page");
    handleError(request, response,
        "Your login is associated with multiple organisations, this is not supported.");
  }

  @ExceptionHandler(value = {Exception.class})
  protected void handleGeneralError(
      HttpServletRequest request,
      HttpServletResponse response,
      Exception ex) throws IOException {
    String exceptionId = UUID.randomUUID().toString();
    LOG.error(
        "Exception occured: {} : {} : {}", exceptionId, ex.getMessage(), ex.getStackTrace());
    handleError(request, response,
        String.format("An error has occured. Id is: %s", exceptionId));
  }

  private void handleError(
      HttpServletRequest request,
      HttpServletResponse response,
      String message) throws IOException {
    String servicePart = UrlHelper.getServicePartOfUrl(request.getRequestURL().toString());
    String env = UrlHelper.getEnvPartOfUrl(request.getRequestURL().toString());

    AuthType authType = authTypeHelper.getAuthTypeFromRequest(request);

    String externalUrl;
    if (authType == AuthType.B2B) {
      externalUrl = proxyConfiguration.getProxyExternalUrlB2b();
    } else {
      externalUrl = proxyConfiguration.getProxyExternalUrlB2c();
    }

    String errorUrl = String.format("%s/error_msg", externalUrl);
    if (!servicePart.isEmpty() && !env.isEmpty()) {
      errorUrl += String.format("/%s/%s", servicePart, env);
    }

    cookieHelper.createCookie(ERROR_MSG, message, 60, response);
    response.sendRedirect(errorUrl);
  }
}