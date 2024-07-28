package uk.gov.defra.tracesx.proxy.filter.zuul;

import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.ContextHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.PatternHelper;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.RequestInfo;
import uk.gov.defra.tracesx.proxy.filter.zuul.helpers.ResponseBodyHelper;

import java.io.IOException;
import java.util.List;

@Component
public class PostPageLinkRewriteFilter extends ZuulFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostPageLinkRewriteFilter.class);

  private final RequestInfo requestInfo;
  private final PatternHelper patternHelper;
  private final ContextHelper contextHelper;
  private final ResponseBodyHelper responseBodyHelper;

  @Autowired
  public PostPageLinkRewriteFilter(
      RequestInfo requestInfo,
      PatternHelper patternHelper,
      ContextHelper contextHelper,
      ResponseBodyHelper responseBodyHelper) {
    this.requestInfo = requestInfo;
    this.patternHelper = patternHelper;
    this.contextHelper = contextHelper;
    this.responseBodyHelper = responseBodyHelper;
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
    RequestContext currentContext = contextHelper.getCurrentRequestContext();
    return requestInfo.shouldReWriteUrls(currentContext.getOriginResponseHeaders());
  }

  @Override
  public Object run() {
    try {
      RequestContext context = contextHelper.getCurrentRequestContext();
      LOGGER.debug("Received code {} status for {}",
          context.getResponseStatusCode(), context.getRequest().getRequestURI());
      if (requestInfo.is400RequestCode(context)) {
        LOGGER.info("Received {} status for {}",
            context.getResponseStatusCode(), context.getRequest().getRequestURI());
      }
      String requestUri = context.getRequest().getRequestURI();
      LOGGER.debug("Rewriting content of {}", context.getRequest().getRequestURI());
      List<String> urlPrefixs = patternHelper.extractProxyPrefix(requestUri);
      String urlPrefix = "/" + urlPrefixs.get(0) + "/" + urlPrefixs.get(1);
      LOGGER.debug("Using url prefix '{}' for content rewrite", urlPrefix);
      String body = responseBodyHelper.readResponseBody(context);
      body = patternHelper.replaceLinksUsingPatterns(body, urlPrefix);
      responseBodyHelper.writeResponseBody(body, context);
    } catch (IOException exception) {
      rethrowRuntimeException(exception);
    }
    return null;
  }
}
