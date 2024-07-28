package uk.gov.defra.tracesx.proxy;

import static java.util.Collections.emptyList;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties
@Getter
@Setter
public class ProxyConfiguration {

  @Value("${server.deploymentEnvironment}")
  private String deploymentEnvironment;
  
  @Value("${azure.clientId}")
  private String clientId;

  @Value("${azure.clientSecret}")
  private String clientSecret;

  @Value("${azure.accessTokenUri}")
  private String accessTokenUri;

  @Value("${azure.userAuthorizationUri}")
  private String userAuthorizationUri;

  @Value("${azure.jwkUrl}")
  private String jwkUrl;

  @Value("${azure.issuer}")
  private String issuer;

  @Value("${azure.logoutRedirectURI}")
  private String logoutRedirectUri;

  @Value("${azure.refreshTokenLifeInSeconds}")
  private int interalRefreshTokenLifeInSeconds;

  @Value("${external.clientId}")
  private String clientIdExternal;

  @Value("${external.clientSecret}")
  private String clientSecretExternal;

  @Value("${external.accessTokenUri}")
  private String accessTokenUriExternal;

  @Value("${external.userAuthorizationUri}")
  private String userAuthorizationUriExternal;

  @Value("${external.jwkUrl}")
  private String jwkUrlExternal;

  @Value("${external.issuer}")
  private String issuerExternal;

  @Value("${external.policyId}")
  private String policyIdExternal;

  @Value("${external.journey}")
  private String journeyExternal;

  @Value("${external.identityServiceId}")
  private String identityServiceIdExternal;

  @Value("${external.gatewayHost}")
  private String gatewayHost;

  @Value("${external.refreshTokenLifeInSeconds}")
  private int externalRefreshTokenLifeInSeconds;

  @Value("${external.logoutRedirectUri}")
  private String logoutRedirectUriExternal;

  @Value("${auth.frontendAuthUsername}")
  private String frontendAuthUsername;

  @Value("${auth.frontendAuthPassword}")
  private String frontendAuthPassword;

  @Value("${auth.soapAuthUsername}")
  private String soapAuthUsername;

  @Value("${auth.soapAuthPassword}")
  private String soapAuthPassword;

  @Value("#{'${allowedRoles.notificationService}'.split(',')}")
  private List<String> notificationServiceAllowedRoles = emptyList();

  @Value("#{'${allowedRoles.decisionService}'.split(',')}")
  private List<String> decisionServiceAllowedRoles = emptyList();

  @Value("#{'${allowedRoles.controlService}'.split(',')}")
  private List<String> controlServiceAllowedRoles = emptyList();

  @Value("#{'${allowedRoles.checksService}'.split(',')}")
  private List<String> checksServiceAllowedRoles = emptyList();

  @Value("#{'${allowedRoles.borderNotificationService}'.split(',')}")
  private List<String> borderNotificationServiceAllowedRoles = emptyList();

  @Value("${azure.b2bCustomerUserId}")
  private String b2bCustomerUserId;

  @Value("${azure.b2bCustomerOrganisationId}")
  private String b2bCustomerOrganisationId;

  @Value("${b2cAllowedRoles.citizen}")
  private UUID citizenRoleId;

  @Value("${b2cAllowedRoles.employee}")
  private UUID employeeRoleId;

  @Value("${b2cAllowedRoles.agentCustomer}")
  private UUID agentCustomerRoleId;

  @Value("${proxyTemplate.frontendTemplate}")
  private String frontendTemplate;

  @Value("${proxyTemplate.serviceTemplate}")
  private String serviceTemplate;

  @Value("${proxyTemplate.notificationUrlTemplate}")
  private String notificationUrlTemplate;

  @Value("${proxyTemplate.decisionUrlTemplate}")
  private String decisionUrlTemplate;

  @Value("${proxyTemplate.controlUrlTemplate}")
  private String controlUrlTemplate;

  @Value("${proxyTemplate.checksUrlTemplate}")
  private String checksUrlTemplate;

  @Value("${proxyTemplate.borderNotificationUrlTemplate}")
  private String borderNotificationUrlTemplate;

  @Value("${landingUrls.control}")
  private String controlLandingUrl;

  @Value("${landingUrls.inspector}")
  private String inspectorLandingUrl;

  @Value("${landingUrls.importer}")
  private String importerLandingUrl;

  @Value("${landingUrls.checks}")
  private String checksLandingUrl;

  @Value("${landingUrls.borderNotification}")
  private String borderNotificationLandingUrl;

  @Value("${enrollment.employeeRoleId}")
  private String enrollmentEmployeeRoleId;

  @Value("${enrollment.serviceId}")
  private String enrollmentServiceId;

  @Value("${auth.cookiePassword}")
  private String cookiePassword;

  @Value("${google.scriptHash}")
  private String googleScriptHash;

  @Value("${proxy.url}")
  private String proxyUrl;

  @Value("${proxy.externalUrlB2C}")
  private String proxyExternalUrlB2c;

  @Value("${proxy.externalUrlB2B}")
  private String proxyExternalUrlB2b;

  @Value("${proxy.jwtKeyPrivateB64}")
  private String jwtKeyPrivate;

  @Value("${proxy.jwtAudience}")
  private String jwtAudience;

  @Value("${proxy.jwtValidForInSeconds}")
  private int jwtValidForInSeconds;

  @Value("${customerDynamics.authUrl}")
  private String customerDynamicsAuthUrl;

  @Value("${customerDynamics.clientId}")
  private String customerDynamicsClientId;

  @Value("${customerDynamics.clientSecret}")
  private String customerDynamicsClientSecret;

  @Value("${customerDynamics.resourceUrl}")
  private String customerDynamicsResourceUrl;

  @Value("${customerDynamics.apiVersion}")
  private String customerDynamicsApiVersion;

  @Value("${customerDynamics.orgAdminRole}")
  private String customerDynamicsOrgAdminRole;
}
