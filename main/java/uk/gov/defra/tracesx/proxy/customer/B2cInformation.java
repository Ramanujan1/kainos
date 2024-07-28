package uk.gov.defra.tracesx.proxy.customer;

import static java.util.stream.Collectors.joining;
import static uk.gov.defra.tracesx.proxy.customer.models.EnrolmentStatus.COMPLETE_APPROVED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.ProxyConfiguration;
import uk.gov.defra.tracesx.proxy.customer.models.Contact;
import uk.gov.defra.tracesx.proxy.customer.models.ContactsResult;
import uk.gov.defra.tracesx.proxy.customer.models.EnrolmentStatus;
import uk.gov.defra.tracesx.proxy.customer.models.LobServiceRole;
import uk.gov.defra.tracesx.proxy.customer.models.LobServiceRolesResult;
import uk.gov.defra.tracesx.proxy.customer.models.LobServiceUserLink;
import uk.gov.defra.tracesx.proxy.customer.models.LobServiceUserLinksResult;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class B2cInformation {

  private static final String FILTER = "filter";
  private static final String SELECT = "select";
  private static final String DYNAMICS_QUERY = "?$filter={filter}&$select={select}";

  private static final String LOBSERVICEUSERLINKS_FILTER_FORMAT = "_defra_serviceuser_value eq {0}";
  private static final String LOBSERVICEUSERLINKS_SELECT_VALUE =
      "_defra_servicerole_value,_defra_organisation_value,defra_enrolmentstatus,"
          + "statecode,statuscode";

  private static final String LOBSERVOCEUSERROLES_SELECT_VALUE = "defra_name";
  private static final String LOBSERVICEROLEID = "defra_lobserivceroleid";

  private static final String CONTACTS_FILTER_FORMAT = "contactid eq {0}";
  private static final String CONTACTS_SELECT_VALUE = "firstname,lastname,fullname";

  private static final int ACTIVE_STATE_CODE = 0;
  private static final int ACTIVE_STATUS_CODE = 1;

  private static final Set<EnrolmentStatus> ALLOWED_ENROLMENT_STATUSES =
      Collections.unmodifiableSet(EnumSet.of(COMPLETE_APPROVED));
  private static final Set<String> ALLOWED_ENROLMENT_STATUS_VALUES =
      Collections.unmodifiableSet(
          ALLOWED_ENROLMENT_STATUSES.stream()
              .map(EnrolmentStatus::getValueAsString)
              .collect(Collectors.toSet()));

  private final DynamicsHttpClient dynamicsHttpClient;
  private final String dynamicsPrefix;
  private final String orgAdminRole;

  @Autowired
  public B2cInformation(DynamicsHttpClient dynamicsHttpClient,
                        ProxyConfiguration proxyConfiguration) {
    this.dynamicsHttpClient = dynamicsHttpClient;
    this.dynamicsPrefix = MessageFormat.format("/api/data/{0}/",
        proxyConfiguration.getCustomerDynamicsApiVersion());
    this.orgAdminRole = proxyConfiguration.getCustomerDynamicsOrgAdminRole();
  }

  public List<LobServiceUserLink> getServiceUserLinks(String contactId) {
    LobServiceUserLinksResult lobServiceUserLinksResult = lobServiceUserLinksSearch(contactId);
    return lobServiceUserLinksResult.getResult()
        .stream()
        .filter(this::isIpaffsRole)
        .filter(this::isEnrollmentAccepted)
        .filter(this::isRoleInCorrectState)
        .collect(Collectors.toList());
  }

  public List<LobServiceRole> getServiceRoles(List<LobServiceUserLink> serviceUserLinks) {
    if (serviceUserLinks.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> serviceRolesIds = extractServiceRoleIds(serviceUserLinks);
    return lobServiceRolesSearch(serviceRolesIds).getResult();
  }

  private List<String> extractServiceRoleIds(List<LobServiceUserLink> serviceUserLinks) {
    return serviceUserLinks.stream()
        .map(LobServiceUserLink::getDefraServiceRoleValue)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private boolean isIpaffsRole(LobServiceUserLink link) {
    return link.getDefraServiceRoleValue() != null
        && !link.getDefraServiceRoleValue().equals(orgAdminRole);
  }

  private boolean isEnrollmentAccepted(LobServiceUserLink lobServiceUserLink) {
    return ALLOWED_ENROLMENT_STATUS_VALUES.contains(lobServiceUserLink.getEnrolmentStatus());
  }

  private boolean isRoleInCorrectState(LobServiceUserLink lobServiceUserLink) {
    return (lobServiceUserLink.getStatecode() == ACTIVE_STATE_CODE
        && lobServiceUserLink.getStatuscode() == ACTIVE_STATUS_CODE);
  }

  private LobServiceRolesResult lobServiceRolesSearch(List<String> lobServiceRoleIds) {
    Map<String, String> params = new HashMap<>(2);
    params.put(FILTER, odataEqualsOrQuery(LOBSERVICEROLEID, lobServiceRoleIds));
    params.put(SELECT, LOBSERVOCEUSERROLES_SELECT_VALUE);
    return dynamicsHttpClient.get(getLobServiceRolesPath(), params, LobServiceRolesResult.class);
  }

  private String odataEqualsOrQuery(String key, List<String> values) {
    return values.stream().map(s -> key + " eq " + s).collect(joining(" or "));
  }

  private LobServiceUserLinksResult lobServiceUserLinksSearch(String contactId) {
    Map<String, String> params = new HashMap<>(2);
    params.put(FILTER, MessageFormat.format(LOBSERVICEUSERLINKS_FILTER_FORMAT, contactId));
    params.put(SELECT, LOBSERVICEUSERLINKS_SELECT_VALUE);
    return dynamicsHttpClient.get(
        getLobServiceUserLinksPath(),
        params,
        LobServiceUserLinksResult.class);
  }

  public Contact getName(String contactId) {
    ContactsResult contactsResult = contactsSearch(contactId);
    return contactsResult.getResult().stream().findFirst()
        .orElse(Contact.builder().firstname("").lastname("").fullname("").build());
  }

  private ContactsResult contactsSearch(String contactId) {
    Map<String, String> params = new HashMap<>(2);
    params.put(FILTER, MessageFormat.format(CONTACTS_FILTER_FORMAT, contactId));
    params.put(SELECT, CONTACTS_SELECT_VALUE);
    return dynamicsHttpClient.get(getContactsPath(), params, ContactsResult.class);
  }

  private String getLobServiceUserLinksPath() {
    return dynamicsPrefix + "defra_lobserviceuserlinks" + DYNAMICS_QUERY;
  }

  private String getLobServiceRolesPath() {
    return dynamicsPrefix + "defra_lobserivceroles" + DYNAMICS_QUERY;
  }

  private String getContactsPath() {
    return dynamicsPrefix + "contacts" + DYNAMICS_QUERY;
  }

}
