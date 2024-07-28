package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

@Component
public class UrlHelper {

  public static final String SOAP_SERVICE_TYPE = "soapsearch";

  private final ProxyConfiguration proxyConfiguration;

  @Autowired
  public UrlHelper(ProxyConfiguration proxyConfiguration) {
    this.proxyConfiguration = proxyConfiguration;
  }

  public URL createRedirectUrl(String envSuffix, String serviceType, String path) {
    try {
      String formatPattern = proxyConfiguration.getFrontendTemplate();
      if (serviceType.equals(SOAP_SERVICE_TYPE)) {
        formatPattern = proxyConfiguration.getServiceTemplate();
      }
      String schemeAndHost = MessageFormat.format(formatPattern, serviceType, envSuffix);
      URL base = new URL(schemeAndHost);
      return new URL(base, path);
    } catch (MalformedURLException exception) {
      throw new RuntimeException(exception);
    }
  }
}
