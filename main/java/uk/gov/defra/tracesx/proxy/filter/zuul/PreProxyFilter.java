package uk.gov.defra.tracesx.proxy.filter.zuul;

import static uk.gov.defra.tracesx.proxy.filter.zuul.helpers.UrlHelper.SOAP_SERVICE_TYPE;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.AccessTokenHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.ContextHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.RequestInfo;

import java.util.Base64;
import java.util.UUID;

@Component
public class PreProxyFilter extends ZuulFilter {

  private final ProxyConfiguration proxyConfiguration;
  private final RequestInfo requestInfo;
  private final ContextHelper contextHelper;
  private final AccessTokenHelper accessTokenHelper;

  private static final Logger LOGGER = LoggerFactory.getLogger(PreProxyFilter.class);

  @Autowired
  public PreProxyFilter(
      ProxyConfiguration proxyConfiguration,
      RequestInfo requestInfo,
      ContextHelper contextHelper,
      AccessTokenHelper accessTokenHelper) {
    this.proxyConfiguration = proxyConfiguration;
    this.requestInfo = requestInfo;
    this.contextHelper = contextHelper;
    this.accessTokenHelper = accessTokenHelper;
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
    RequestContext requestContext = contextHelper.getCurrentRequestContext();
    // TODO: only run if user details is populated
    String accessTokenOnly = accessTokenHelper.getAccessToken();
    String conversationId = UUID.randomUUID().toString();
    requestContext.addZuulRequestHeader("INS-ConversationId", conversationId);
    LOGGER.info("Conversation id: {} for {}", conversationId, requestContext.getRouteHost());
    requestContext.addZuulRequestHeader("Authorization",
        String.format("Bearer %s", accessTokenOnly));
    requestContext.addZuulRequestHeader("x-auth-basic",
        "Basic " + getXBasicAuthHeader(requestContext));

    return requestContext;
  }

  private String getXBasicAuthHeader(RequestContext requestContext) {

    String userName = proxyConfiguration.getFrontendAuthUsername();
    String userPassword = proxyConfiguration.getFrontendAuthPassword();

    String requestPath = requestContext.getRequest().getRequestURI();
    if (requestPath.startsWith(String.format("/%s", SOAP_SERVICE_TYPE))) {
      userName = proxyConfiguration.getSoapAuthUsername();
      userPassword = proxyConfiguration.getSoapAuthPassword();
    }

    return new String(
        Base64.getEncoder().encode(String.format("%s:%s", userName, userPassword).getBytes()));
  }
}
