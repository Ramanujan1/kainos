package uk.gov.defra.tracesx.proxy.filter.zuul;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.ContextHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.HeaderHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.RequestInfo;

import java.util.List;

@Component
public class PostRedirectRewriteFilter extends ZuulFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostRedirectRewriteFilter.class);

  private final RequestInfo requestInfo;
  private final ContextHelper contextHelper;
  private final HeaderHelper headerHelper;

  @Autowired
  public PostRedirectRewriteFilter(
      RequestInfo requestInfo, ContextHelper contextHelper, HeaderHelper headerHelper) {
    this.requestInfo = requestInfo;
    this.contextHelper = contextHelper;
    this.headerHelper = headerHelper;
  }

  @Override
  public String filterType() {
    return FilterConstants.POST_TYPE;
  }

  @Override
  public int filterOrder() {
    return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
  }

  @Override
  public boolean shouldFilter() {
    return requestInfo.is300RequestCode(contextHelper.getCurrentRequestContext());
  }

  // FIXME: might be able to use an existing filter:
  // https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html#zuul-redirect-location-rewrite
  @Override
  public Object run() {
    LOGGER.debug("Rewriting headers on an HTTP redirect");
    RequestContext context = contextHelper.getCurrentRequestContext();
    List<Pair<String, String>> zuulHeaders = context.getZuulResponseHeaders();
    List<Pair<String, String>> rewroteHeaders = headerHelper.rewriteHeaders(zuulHeaders);
    context.put("zuulResponseHeaders", rewroteHeaders);
    return null;
  }
}
