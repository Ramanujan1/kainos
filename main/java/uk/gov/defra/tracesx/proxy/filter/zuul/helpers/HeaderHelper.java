package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import com.netflix.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;

import java.util.ArrayList;
import java.util.List;

@Component
public class HeaderHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(PatternHelper.class);
  private PatternHelper patternHelper;
  private ContextHelper contextHelper;
  private final ProxyConfiguration proxyConfiguration;

  private static final String NOTIFICATION_HOME = "protected/notifications";
  private static final String BIP_HOME = "protected/bip-notifications";
  private static final String OV_HOME = "protected/ov-notifications";
  private static final String FSA_HOME = "protected/fsa-dashboard";
  private static final String BORDER_NOTIFICATION_HOME = "protected/border-notifications";

  @Autowired
  public HeaderHelper(
      PatternHelper patternHelper,
      ContextHelper contextHelper,
      ProxyConfiguration proxyConfiguration) {
    this.patternHelper = patternHelper;
    this.contextHelper = contextHelper;
    this.proxyConfiguration = proxyConfiguration;
  }

  public List<Pair<String, String>> rewriteHeaders(List<Pair<String, String>> currentZuulHeaders) {
    List<Pair<String, String>> rewroteHeaders = new ArrayList<>(currentZuulHeaders.size());
    for (Pair<String, String> header : currentZuulHeaders) {
      header = rewriteLocation(header);
      rewroteHeaders.add(header);
    }
    return rewroteHeaders;
  }

  private Pair<String, String> rewriteLocation(Pair<String, String> zuulHeader) {
    String requestUri = contextHelper.getCurrentRequestContext().getRequest().getRequestURI();
    List<String> urlPrefixs = patternHelper.extractProxyPrefix(requestUri);
    if (zuulHeader.first().equalsIgnoreCase("location")) {
      String zuulLocationHeader = zuulHeader.second();
      String location = getUrlPrefixFromPath(zuulLocationHeader, urlPrefixs) + zuulLocationHeader;
      LOGGER.debug("Replaced Location {} with {}", zuulLocationHeader, location);
      return new Pair<>(zuulHeader.first(), location);
    }
    return zuulHeader;
  }

  private String getUrlPrefixFromPath(String path, List<String> urlPrefixs) {
    String environment = urlPrefixs.get(1);
    if (path.contains(NOTIFICATION_HOME)) {
      return createUrlPrefix(proxyConfiguration.getNotificationUrlTemplate(), environment);
    } else if (path.contains(BIP_HOME)) {
      return createUrlPrefix(proxyConfiguration.getDecisionUrlTemplate(), environment);
    } else if (path.contains(OV_HOME)) {
      return createUrlPrefix(proxyConfiguration.getControlUrlTemplate(), environment);
    } else if (path.contains(FSA_HOME)) {
      return createUrlPrefix(proxyConfiguration.getChecksUrlTemplate(), environment);
    } else if (path.contains(BORDER_NOTIFICATION_HOME)) {
      return createUrlPrefix(proxyConfiguration.getBorderNotificationUrlTemplate(), environment);
    } else {
      return createUrlPrefix(urlPrefixs.get(0), environment);
    }
  }

  private String createUrlPrefix(String service, String environment) {
    return "/" + service + "/" + environment;
  }
}
