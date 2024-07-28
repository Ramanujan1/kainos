package uk.gov.defra.tracesx.proxy.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;
import uk.gov.defra.tracesx.proxy.security.helpers.LogoutHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/protected/logout")
public class LogOutResource {

  private final LogoutHelper logoutHelper;
  private final CookieHelper cookieHelper;

  @Autowired
  public LogOutResource(LogoutHelper logoutHelper, CookieHelper cookieHelper) {
    this.logoutHelper = logoutHelper;
    this.cookieHelper = cookieHelper;
  }

  @GetMapping
  public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
    RedirectView redirectView = new RedirectView();
    cookieHelper.deleteAllCookies(request, response);
    redirectView.setUrl(logoutHelper.getRedirectUrl(request));
    return redirectView;
  }

}
