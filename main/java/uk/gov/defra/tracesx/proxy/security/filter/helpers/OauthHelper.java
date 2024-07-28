package uk.gov.defra.tracesx.proxy.security.filter.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.customer.B2cEnrollment;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.exceptions.DecodeException;
import uk.gov.defra.tracesx.proxy.security.helpers.TokenHelper;
import uk.gov.defra.tracesx.proxy.security.jwt.JwtService;
import uk.gov.defra.tracesx.proxy.security.models.tokens.IdTokenResponseModel;
import uk.gov.defra.tracesx.proxy.security.models.tokens.RawTokenResponseModel;

import java.io.IOException;
import java.net.URLDecoder;

@Component
public class OauthHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(OauthHelper.class);
  private static final String CHARSET = "UTF-8";

  private final ProxyConfiguration proxyConfiguration;
  private final RestTemplate restTemplate;
  private final TokenHelper tokenHelper;
  private final OauthHttpCalls oauthUtils;
  private final JwtService jwtService;
  private final B2cEnrollment b2cEnrollment;
  private final ObjectMapper mapper = new ObjectMapper();

  public OauthHelper(ProxyConfiguration proxyConfiguration, RestTemplate restTemplate,
                     TokenHelper tokenHelper, OauthHttpCalls oauthUtils, JwtService jwtService,
                     B2cEnrollment b2cEnrollment) {
    this.proxyConfiguration = proxyConfiguration;
    this.restTemplate = restTemplate;
    this.tokenHelper = tokenHelper;
    this.oauthUtils = oauthUtils;
    this.jwtService = jwtService;
    this.b2cEnrollment = b2cEnrollment;
  }

  public boolean validResponseFromAuthProvider(String state, String originalState) {
    boolean isValid = false;
    try {
      if (state.equals(URLDecoder.decode(originalState, CHARSET))) {
        isValid = true;
      }
    } catch (IOException ex) {
      LOGGER.info("State received did not match original state: {}", ex.getMessage());
    }
    return isValid;
  }

  public String requestAndCreateAccessToken(String userSuppliedCode, AuthType authType)
      throws DecodeException {

    String accessToken = null;

    try {
      ResponseEntity<String> authResponse = oauthUtils.makeHttpCallForAccessTokens(
          userSuppliedCode, authType, proxyConfiguration, restTemplate);

      if (authResponse.getStatusCode() == HttpStatus.OK) {
        String externalToken = extractExternalAccessToken(authResponse, authType);
        accessToken = exchangeForProxyToken(externalToken, authType);
      } else {
        LOGGER.error("Request rejected for access tokens, status was {}",
            authResponse.getStatusCode());
      }

    } catch (IOException | NumberFormatException | RestClientException
        | AuthenticationServiceException ex) {
      LOGGER.info("Failed to get access tokens: {}", ex.getMessage());
      accessToken = null;
    }

    return accessToken;
  }

  private String exchangeForProxyToken(String externalToken, AuthType authType)
      throws IOException, DecodeException {

    if (authType == AuthType.B2C) {
      b2cEnrollment.checkB2cEnrollment(externalToken);
    }

    return jwtService.getProxyAccessToken(externalToken, authType);
  }

  private String extractExternalAccessToken(ResponseEntity<String> authResponse, AuthType authType)
      throws IOException {
    String authResponseString = authResponse.getBody();
    String externalToken;
    if (authType == AuthType.B2C) {
      IdTokenResponseModel idTokenResponseModel =
          mapper.readValue(authResponseString, IdTokenResponseModel.class);
      externalToken = idTokenResponseModel.getAccessToken();
    } else {
      RawTokenResponseModel rawTokenResponseModel =
          mapper.readValue(authResponseString, RawTokenResponseModel.class);
      externalToken = rawTokenResponseModel.getAccessToken();
    }
    if (!tokenHelper.tokenIsValid(externalToken, authType)) {
      throw new AuthenticationServiceException("Invalid External Tokens Returned");
    }
    return externalToken;
  }
}
