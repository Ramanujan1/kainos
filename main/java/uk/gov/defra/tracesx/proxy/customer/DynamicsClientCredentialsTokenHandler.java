package uk.gov.defra.tracesx.proxy.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.customer.models.DynamicsToken;

import java.util.Date;

@Component
public class DynamicsClientCredentialsTokenHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DynamicsClientCredentialsTokenHandler.class);
  private static final String GRANT_TYPE = "grant_type";
  private static final String CLIENT_ID = "client_id";
  private static final String CLIENT_SECRET = "client_secret";
  private static final String RESOURCE = "resource";
  private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";

  private final RestTemplate httpClient;
  private final ProxyConfiguration proxyConfiguration;
  private static final int REFRESH_TOKEN_EXPIRY_TOLERANCE = 30;

  DynamicsToken dynamicsToken;

  public DynamicsClientCredentialsTokenHandler(
      RestTemplate httpClient,
      ProxyConfiguration proxyConfiguration) {
    this.httpClient = httpClient;
    this.proxyConfiguration = proxyConfiguration;
  }

  public synchronized String getToken(boolean forceNewToken) {
    try {
      if (dynamicsToken == null || forceNewToken || isExpired(dynamicsToken)) {
        dynamicsToken =
            httpClient.postForObject(
                proxyConfiguration.getCustomerDynamicsAuthUrl(),
                getAuthFormData(),
                DynamicsToken.class);
        LOGGER.info("Dynamics token refreshing");
      }

      return dynamicsToken.getAccessToken();

    } catch (HttpStatusCodeException exception) {
      LOGGER.error("Failed to get dynamics token. HTTP Error {}: {}",
          exception.getStatusCode(), exception.getMessage());
      throw exception;
    }
  }

  private static boolean isExpired(DynamicsToken dynamicsToken) {
    Date now = new Date();
    return new Date((dynamicsToken.getExpiresOn() - REFRESH_TOKEN_EXPIRY_TOLERANCE) * 1000L)
        .before(now);
  }

  private MultiValueMap<String, String> getAuthFormData() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add(GRANT_TYPE, CLIENT_CREDENTIALS_GRANT_TYPE);
    formData.add(CLIENT_ID, proxyConfiguration.getCustomerDynamicsClientId());
    formData.add(CLIENT_SECRET, proxyConfiguration.getCustomerDynamicsClientSecret());
    formData.add(RESOURCE, proxyConfiguration.getCustomerDynamicsResourceUrl());
    return formData;
  }
}
