package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import com.netflix.util.Pair;

import java.util.List;

public class ContentTypeHelper {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final Pair<String, String> BLANK_PAIR = new Pair<>("", "");

  public Pair<String, String> getContentType(List<Pair<String, String>> headers) {
    if (headers == null) {
      return BLANK_PAIR;
    }
    return headers.stream()
        .filter(header -> header.first().equalsIgnoreCase(CONTENT_TYPE))
        .findFirst()
        .orElse(BLANK_PAIR);
  }
}
