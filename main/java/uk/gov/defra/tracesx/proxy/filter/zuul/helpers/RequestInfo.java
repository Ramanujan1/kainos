package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestInfo {

  private static final String HTML_CONTENT_TYPE = "text/html";

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RequestInfo.class);

  ContentTypeHelper contentTypeHelper = new ContentTypeHelper();

  private boolean runZuulFilters = true;

  public boolean isRunZuulFilters() {
    return runZuulFilters;
  }

  public boolean is200RequestCode(RequestContext currentContext) {
    return currentContext.getResponseStatusCode() < 300;
  }

  public boolean is300RequestCode(RequestContext currentContext) {
    int responseStatusCode = currentContext.getResponseStatusCode();
    return responseStatusCode >= 300 && responseStatusCode < 400;
  }

  public boolean is400RequestCode(RequestContext currentContext) {
    return currentContext.getResponseStatusCode() >= 400;
  }

  public void setRunZuulFilters(boolean runZuulFilters) {
    this.runZuulFilters = runZuulFilters;
  }

  public boolean shouldReWriteUrls(List<Pair<String, String>> originResponseHeaders) {

    Pair<String, String> contentTypeHeader =
        contentTypeHelper.getContentType(originResponseHeaders);
    String contentType = contentTypeHeader.second();

    if (contentType == null) {
      LOGGER.info("content-type is null");
      return false;
    }
    return contentType.contains(HTML_CONTENT_TYPE);
  }
}
