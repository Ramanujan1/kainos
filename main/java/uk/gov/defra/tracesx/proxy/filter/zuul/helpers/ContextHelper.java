package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

@Component
public class ContextHelper {
  public RequestContext getCurrentRequestContext() {
    return getCurrentContext();
  }
}
