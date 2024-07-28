package uk.gov.defra.tracesx.proxy.customer;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.customer.models.Connection;
import uk.gov.defra.tracesx.proxy.customer.models.ConnectionsResult;
import uk.gov.defra.tracesx.proxy.customer.models.CustomerEnrollmentData;
import uk.gov.defra.tracesx.proxy.customer.models.CustomerLink;
import uk.gov.defra.tracesx.proxy.customer.models.EnrollContact;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerUtils {
  private static final int COMPLETE_APPROVED = 3;
  private static final int DESIRED_ENROLMENT_STATUS = COMPLETE_APPROVED;
  private static final String DEFRA_CONNECTIONS_PATH =
      "connections?$filter=_record1id_value eq {0}";

  public List<CustomerLink> connectionsToLinks(ConnectionsResult connectionsResult,
                                               String customerId) {
    return connectionsResult.getConnections().stream()
        .map(c -> connectionToLink(c, customerId))
        .collect(Collectors.toList());
  }

  private CustomerLink connectionToLink(Connection connection, String customerId) {
    return CustomerLink.builder()
        .organisationId(getOrganisationId(connection, customerId))
        .connectionDetailsId(connection.getConnectionDetails())
        .customerRoleId(connection.getRecord1RoleId())
        .connectionId(connection.getConnectionId())
        .build();
  }

  private String getOrganisationId(Connection connection, String customerId) {
    if (connection.getRecord1IdValue().equals(customerId)) {
      return connection.getRecord2IdValue();
    }
    return connection.getRecord1IdValue();
  }

  public CustomerLink findActiveCustomerLink(
      List<CustomerLink> customerLinks,
      UUID employeeRoleUuid,
      UUID agentCustomerRoleUuid,
      UUID citizenRoleUuid) {

    Optional<CustomerLink> agentLink = empty();
    Optional<CustomerLink> citizenLink = empty();

    String employeeRoleId = employeeRoleUuid.toString();
    String agentCustomerRoleId = agentCustomerRoleUuid.toString();
    String citizenRoleId = citizenRoleUuid.toString();

    for (CustomerLink customerLink : customerLinks) {
      String customerRoleId = customerLink.getCustomerRoleId();
      if (employeeRoleId.equals(customerRoleId)) {
        return customerLink;
      }
      if (!agentLink.isPresent() && agentCustomerRoleId.equals(customerRoleId)) {
        agentLink = Optional.of(customerLink);
      }
      if (!citizenLink.isPresent() && citizenRoleId.equals(customerRoleId)) {
        citizenLink = Optional.of(customerLink);
      }
    }

    return agentLink
        .orElse(citizenLink
            .orElse(null));
  }

  public String buildConnectionsUrl(String customerId) {
    return MessageFormat.format(DEFRA_CONNECTIONS_PATH, customerId);
  }

  public EnrollContact enrollmentDataToEnrollContact(CustomerEnrollmentData data) {

    String organisation = ofNullable(data.getOrganisation()).map(org ->
        format("/accounts(%s)", org)).orElse(null);

    return EnrollContact.builder()
        .conntectionDetail(format("/defra_connectiondetailses(%s)", data.getConnectionDetailsId()))
        .serviceUser(format("/contacts(%s)", data.getCustomerId()))
        .enrolmentStatus(DESIRED_ENROLMENT_STATUS)
        .organisation(organisation)
        .serviceRole(format("/defra_lobserivceroles(%s)", data.getLobServiceRoleId()))
        .build();
  }
}
