package uk.gov.defra.tracesx.proxy.filter.zuul.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.exceptions.InvalidUrlException;
import uk.gov.defra.tracesx.proxy.exceptions.PatternException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PatternHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(PatternHelper.class);
  private final ProxyConfiguration proxyConfiguration;

  private Pattern genericLinkPattern =
      Pattern.compile(readPatternFromResource("/genericLink.pattern"));
  private Pattern notificationLinkPattern =
      Pattern.compile(readPatternFromResource("/notificationLink.pattern"));
  private Pattern decisionLinkPattern =
      Pattern.compile(readPatternFromResource("/decisionLink.pattern"));
  private Pattern controlLinkPattern =
      Pattern.compile(readPatternFromResource("/controlLink.pattern"));
  private Pattern checksLinkPattern =
      Pattern.compile(readPatternFromResource("/checksLink.pattern"));
  private Pattern borderNotificationLinkPattern =
      Pattern.compile(readPatternFromResource("/borderNotificationLink.pattern"));

  @Autowired
  public PatternHelper(ProxyConfiguration proxyConfiguration) {
    this.proxyConfiguration = proxyConfiguration;
  }

  String readPatternFromResource(String resource) {
    try {
      InputStream is = PatternHelper.class.getResourceAsStream(resource);
      String pattern = StreamUtils.copyToString(is, Charset.forName("UTF-8"));
      if (pattern.isEmpty()) {
        throw new PatternException("No pattern found when loading pattern with name " + resource);
      }
      LOGGER.debug("Loaded pattern {}", pattern);
      return pattern;
    } catch (IOException exception) {
      LOGGER.error("Failed to load pattern file with exception {}", exception.getMessage());
      throw new PatternException(exception);
    }
  }

  /**
   * Extracts the first two parts from a path which forms the "prefix" used by the proxy.
   *
   * <p>/ENV/SERVICE/the/rest/of/the/path
   *
   * @param path a path with at least two parts
   * @return List of proxy prefix's
   */
  public List<String> extractProxyPrefix(String path) {
    String[] parts = path.split("\\/");
    try {
      return Arrays.asList(parts[1], parts[2]);
    } catch (ArrayIndexOutOfBoundsException exception) {
      throw new InvalidUrlException(
          String.format("Invalid url when extracting proxy prefix: %s", exception));
    }
  }

  private String replaceLinkUsingPattern(String body, String urlPrefix, Pattern linkPattern) {
    Matcher matcher = linkPattern.matcher(body);
    LOGGER.debug("Found at least one url to replace: {}", matcher.matches());
    String replacement = MessageFormat.format("$1=\"{0}$2\"", urlPrefix);
    return matcher.replaceAll(replacement);
  }

  public String replaceLinksUsingPatterns(String body, String defaultUrlPrefix) {
    String environment = extractProxyPrefix(defaultUrlPrefix).get(1);
    String replacedBody = replaceLinkUsingPattern(body, defaultUrlPrefix, genericLinkPattern);
    replacedBody =
        replaceLinkUsingPattern(
            replacedBody,
            createUrlPrefix(proxyConfiguration.getNotificationUrlTemplate(), environment),
            notificationLinkPattern);
    replacedBody =
        replaceLinkUsingPattern(
            replacedBody,
            createUrlPrefix(proxyConfiguration.getDecisionUrlTemplate(), environment),
            decisionLinkPattern);
    replacedBody =
        replaceLinkUsingPattern(
            replacedBody,
            createUrlPrefix(proxyConfiguration.getControlUrlTemplate(), environment),
            controlLinkPattern);
    replacedBody =
        replaceLinkUsingPattern(
            replacedBody,
            createUrlPrefix(proxyConfiguration.getChecksUrlTemplate(), environment),
            checksLinkPattern);
    replacedBody =
        replaceLinkUsingPattern(
            replacedBody,
            createUrlPrefix(proxyConfiguration.getBorderNotificationUrlTemplate(), environment),
            borderNotificationLinkPattern);
    return replacedBody;
  }

  private String createUrlPrefix(String service, String environment) {
    return "/" + service + "/" + environment;
  }
}
