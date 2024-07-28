package uk.gov.defra.tracesx.proxy.customer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.customer.models.EnrollContact;
import uk.gov.defra.tracesx.proxy.customer.models.EnrollmentResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Component
public class DynamicsHttpClient {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DynamicsHttpClient.class);
  public static final java.lang.String AUTHORIZATION = "Authorization";
  public static final java.lang.String BEARER = "Bearer ";

  private final DynamicsClientCredentialsTokenHandler dynamicsClientCredentialsTokenHandler;
  private final RestTemplate httpClient;
  private final ProxyConfiguration proxyConfiguration;

  @Autowired
  public DynamicsHttpClient(
      DynamicsClientCredentialsTokenHandler dynamicsClientCredentialsTokenHandler,
      RestTemplate httpClient,
      ProxyConfiguration proxyConfiguration) {
    this.dynamicsClientCredentialsTokenHandler = dynamicsClientCredentialsTokenHandler;
    this.httpClient = httpClient;
    this.proxyConfiguration = proxyConfiguration;
  }

  /**
   * @param path       relative path of the dynamics API operation and the query parameters with
   *                   RestTemplate uri parameters.
   * @param params     the RestTemplate uri parameters
   * @param returnType deserialize the response into this type
   * @param <T> return type
   * @return the deserialized response
   */
  public <T> T get(String path, Map<String, String> params, Class<T> returnType) {
    String url = url(path);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(
        AUTHORIZATION, BEARER + dynamicsClientCredentialsTokenHandler.getToken(false));
    HttpEntity httpEntity = new HttpEntity(httpHeaders);

    try {
      return httpClient.exchange(url, HttpMethod.GET, httpEntity, returnType, params).getBody();
    } catch (RestClientException exception) {
      LOGGER.error("Dynamics call failed, retrying with new token: {}", exception.getMessage());
      httpHeaders.set(
          AUTHORIZATION, BEARER + dynamicsClientCredentialsTokenHandler.getToken(true));
      return httpClient.exchange(url, HttpMethod.GET, httpEntity, returnType, params).getBody();
    }
  }

  private String url(String path) {
    try {
      return new URL(new URL(proxyConfiguration.getCustomerDynamicsResourceUrl()), path).toString();
    } catch (MalformedURLException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void enrolUser(String url, EnrollContact map) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(
        AUTHORIZATION, BEARER + dynamicsClientCredentialsTokenHandler.getToken(false));
    httpHeaders.set("Prefer", "odata.maxpagesize=500, odata.include-annotations=\"*\"");
    HttpEntity entity = new HttpEntity(map, httpHeaders);

    try {
      httpClient.postForEntity(url, entity, EnrollmentResult.class);
    } catch (RestClientException exception) {
      LOGGER.error("Dynamics call failed, retrying with new token: {}", exception.getMessage());
      httpHeaders.set(
          AUTHORIZATION, BEARER + dynamicsClientCredentialsTokenHandler.getToken(true));
      httpClient.postForEntity(url, entity, EnrollmentResult.class);
    }
  }
}
