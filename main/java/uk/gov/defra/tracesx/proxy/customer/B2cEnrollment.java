package uk.gov.defra.tracesx.proxy.customer;

import static uk.gov.defra.tracesx.proxy.security.jwt.JwtTokenGenerator.CONTACT_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.customer.models.ConnectionsResult;
import uk.gov.defra.tracesx.proxy.customer.models.CustomerEnrollmentData;
import uk.gov.defra.tracesx.proxy.customer.models.CustomerLink;
import uk.gov.defra.tracesx.proxy.customer.models.EnrollContact;
import uk.gov.defra.tracesx.proxy.customer.models.EnrollmentType;
import uk.gov.defra.tracesx.proxy.enums.AuthType;
import uk.gov.defra.tracesx.proxy.exceptions.DecodeException;
import uk.gov.defra.tracesx.proxy.exceptions.EnrolmentException;
import uk.gov.defra.tracesx.proxy.security.helpers.TokenHelper;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class B2cEnrollment {

  private final DynamicsHttpClient dynamicsHttpClient;
  private final ProxyConfiguration proxyConfiguration;
  private final TokenHelper tokenHelper;
  private final B2cRoles b2cRoles;
  private final CustomerUtils customerUtils;
  private final String dynamicsPrefix;

  @Autowired
  public B2cEnrollment(
      B2cRoles b2cRoles,
      DynamicsHttpClient dynamicsHttpClient,
      TokenHelper tokenHelper,
      ProxyConfiguration proxyConfiguration,
      CustomerUtils customerUtils) {
    this.dynamicsHttpClient = dynamicsHttpClient;
    this.proxyConfiguration = proxyConfiguration;
    this.tokenHelper = tokenHelper;
    this.b2cRoles = b2cRoles;
    this.customerUtils = customerUtils;
    this.dynamicsPrefix = MessageFormat.format("/api/data/{0}/",
        proxyConfiguration.getCustomerDynamicsApiVersion());
  }

  public void checkB2cEnrollment(String proxyAccessToken) throws IOException, DecodeException {

    Map<Object, Object> claims =
        tokenHelper.getDecodedClaims(
            proxyAccessToken, AuthType.B2C);
    String contactId = (String) claims.get(CONTACT_ID);

    if (userIsNotEnrolled(contactId)) {
      List<CustomerLink> customerLinks = getCustomerLinks(contactId);
      CustomerLink customerLinkActive =
          customerUtils.findActiveCustomerLink(
              customerLinks,
              proxyConfiguration.getEmployeeRoleId(),
              proxyConfiguration.getAgentCustomerRoleId(),
              proxyConfiguration.getCitizenRoleId());
      if (customerLinkActive != null) {
        enrollUserAsNotifier(customerLinkActive, contactId);
      } else {
        throw new EnrolmentException("No active customer link to enroll user.");
      }
    }
  }

  private boolean userIsNotEnrolled(String contactId) {
    return b2cRoles.getRolesWithMapping(contactId).isEmpty();
  }

  private List<CustomerLink> getCustomerLinks(String customerId) {
    String customerLinksPath = dynamicsPrefix + customerUtils.buildConnectionsUrl(customerId);
    ConnectionsResult connectionsResult =
        dynamicsHttpClient.get(customerLinksPath, Collections.emptyMap(), ConnectionsResult.class);
    return customerUtils.connectionsToLinks(connectionsResult, customerId);
  }

  private void enrollUserAsNotifier(CustomerLink customerLink, String contactId) {
    EnrollmentType enrollmentType = getEnrollmentType(customerLink);
    String organisationId = getOrganisationId(enrollmentType, customerLink);

    CustomerEnrollmentData enrollmentData =
        CustomerEnrollmentData.builder()
            .lobServiceRoleId(proxyConfiguration.getEnrollmentServiceId())
            .customerId(contactId)
            .connectionDetailsId(customerLink.getConnectionDetailsId())
            .organisation(organisationId)
            .build();

    EnrollContact contact = customerUtils.enrollmentDataToEnrollContact(enrollmentData);

    String enrollmentUrl = proxyConfiguration.getCustomerDynamicsResourceUrl()
        + dynamicsPrefix
        + "defra_lobserviceuserlinks";
    dynamicsHttpClient.enrolUser(enrollmentUrl, contact);
  }

  private EnrollmentType getEnrollmentType(CustomerLink customerLink) {
    if (customerLink
        .getCustomerRoleId()
        .equals(proxyConfiguration.getCitizenRoleId().toString())) {
      return EnrollmentType.CITIZEN;
    }
    return EnrollmentType.OTHER;
  }

  private String getOrganisationId(EnrollmentType enrollmentType, CustomerLink customerLink) {
    if (enrollmentType.equals(EnrollmentType.CITIZEN)) {
      return null;
    }
    return customerLink.getOrganisationId();
  }
}
