package uk.gov.defra.tracesx.proxy.security.filter.helpers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.enums.AuthType;

@Component
public class OauthHttpCalls {

  private static final String GRANT_TYPE = "grant_type";
  private static final String CLIENT_ID = "client_id";
  private static final String CLIENT_SECRET = "client_secret";
  private static final String RESOURCE = "resource";
  private static final String REFRESH_TOKEN = "refreshToken";

  public ResponseEntity<String> makeHttpCallForAccessTokens(
      String code,
      AuthType authType,
      ProxyConfiguration proxyConfiguration,
      RestTemplate restTemplate) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    String accessTokenUri;
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    if (authType == AuthType.B2C) {
      map.add(GRANT_TYPE, "authorization_code");
      map.add(CLIENT_ID, proxyConfiguration.getClientIdExternal());
      map.add("code", code);
      map.add("redirect_uri", proxyConfiguration.getProxyExternalUrlB2c());
      map.add(CLIENT_SECRET, proxyConfiguration.getClientSecretExternal());
      map.add(RESOURCE, proxyConfiguration.getClientIdExternal());
      accessTokenUri = proxyConfiguration.getAccessTokenUriExternal();
    } else {
      map.add(GRANT_TYPE, "authorization_code");
      map.add(CLIENT_ID, proxyConfiguration.getClientId());
      map.add("code", code);
      map.add("redirect_uri", proxyConfiguration.getProxyExternalUrlB2b());
      map.add(CLIENT_SECRET, proxyConfiguration.getClientSecret());
      map.add(RESOURCE, proxyConfiguration.getClientId());
      accessTokenUri = proxyConfiguration.getAccessTokenUri();
    }
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    return restTemplate.postForEntity(accessTokenUri, request, String.class);
  }

  public ResponseEntity<String> makeHttpCallForTokenRefresh(
      String refreshToken, AuthType authType,
      ProxyConfiguration proxyConfiguration,
      RestTemplate restTemplate) {
    String accessTokenUri;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    if (authType == AuthType.B2C) {
      map.add(GRANT_TYPE, REFRESH_TOKEN);
      map.add(CLIENT_ID, proxyConfiguration.getClientIdExternal());
      map.add(REFRESH_TOKEN, refreshToken);
      map.add(RESOURCE, proxyConfiguration.getClientIdExternal());
      map.add(CLIENT_SECRET, proxyConfiguration.getClientSecretExternal());
      accessTokenUri = proxyConfiguration.getAccessTokenUriExternal();
    } else {
      map.add(GRANT_TYPE, REFRESH_TOKEN);
      map.add(CLIENT_ID, proxyConfiguration.getClientId());
      map.add(REFRESH_TOKEN, refreshToken);
      map.add(RESOURCE, proxyConfiguration.getClientId());
      map.add(CLIENT_SECRET, proxyConfiguration.getClientSecret());
      accessTokenUri = proxyConfiguration.getAccessTokenUri();
    }
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    return restTemplate.postForEntity(accessTokenUri, request, String.class);
  }

}
