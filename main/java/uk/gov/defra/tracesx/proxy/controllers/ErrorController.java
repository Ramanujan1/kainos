package uk.gov.defra.tracesx.proxy.controllers;

import static uk.gov.defra.tracesx.proxy.exceptions.GlobalExceptionHandler.ERROR_MSG;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.defra.tracesx.proxy.security.helpers.CookieHelper;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ErrorController {

  private static final String GENERIC_ERROR_MSG = "Sorry there has been a problem";
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
  private final CookieHelper cookieHelper;

  @Autowired
  public ErrorController(CookieHelper cookieHelper) {
    this.cookieHelper = cookieHelper;
  }

  @GetMapping("/error_msg")
  public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
    return returnErrorPage(request, model, response);
  }

  private String returnErrorPage(
      HttpServletRequest request,
      Model model,
      HttpServletResponse response) {
    String errorMessage = null;
    try {
      errorMessage = cookieHelper.getCookie(ERROR_MSG, request);
    } catch (UnsupportedEncodingException ex) {
      LOGGER.error("Failed to get error message from cookie: {}", ex.getMessage());
    }

    model.addAttribute("data", ObjectUtils.firstNonNull(errorMessage, GENERIC_ERROR_MSG));
    response.setStatus(500);
    return "error_msg";
  }
}
