package uk.gov.defra.tracesx.proxy.filter.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.ContextHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.RequestInfo;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.UrlHelper;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PreDynamicRoutingFilter extends ZuulFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PreDynamicRoutingFilter.class);

  private final Pattern routePattern = Pattern.compile("\\/([^\\/]*)\\/([^\\/]*)(.*)");
  private final RequestInfo requestInfo;
  private final ContextHelper contextHelper;
  private final UrlHelper urlHelper;

  @Autowired
  public PreDynamicRoutingFilter(
      RequestInfo requestInfo,
      ContextHelper contextHelper,
      UrlHelper urlHelper) {
    this.requestInfo = requestInfo;
    this.contextHelper = contextHelper;
    this.urlHelper = urlHelper;
  }

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return FilterConstants.SEND_FORWARD_FILTER_ORDER;
  }

  @Override
  public boolean shouldFilter() {
    return requestInfo.isRunZuulFilters();
  }

  @Override
  public Object run() {
    RequestContext context = contextHelper.getCurrentRequestContext();
    String requestPath = context.getRequest().getRequestURI();
    LOGGER.debug("request path: {}", requestPath);
    Matcher matcher = routePattern.matcher(requestPath);
    LOGGER.debug("applied pattern: {}", matcher.pattern());
    if (matcher.matches()) {
      String serviceType = matcher.group(1);
      String envSuffix = matcher.group(2);
      String path = matcher.group(3);
      LOGGER.debug("envSuffix: {} serviceType: {} path: {}", envSuffix, serviceType, path);
      URL url = urlHelper.createRedirectUrl(envSuffix, serviceType, path);
      LOGGER.debug("redirect url: {}", url);
      context.set("requestURI", ""); // FIXME: do we want to save this somewhere else?
      context.removeRouteHost();
      context.setRouteHost(url);

      LOGGER.debug("Forwarding request to: {}", url);

    } else {
      LOGGER.warn("Unable to map request to an internal host");
    }
    return null;
  }
}
