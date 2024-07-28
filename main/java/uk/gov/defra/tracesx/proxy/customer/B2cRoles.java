package uk.gov.defra.tracesx.proxy.customer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.proxy.customer.models.LobServiceUserLink;
import uk.gov.defra.tracesx.proxy.exceptions.MultipleOrganisationsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class B2cRoles {

  private final B2cInformation b2cInformation;
  private static final String ACTIVE_STATUS_IN_ORGANISATION = "3";


  @Autowired
  public B2cRoles(B2cInformation b2cInformation) {
    this.b2cInformation = b2cInformation;
  }

  public List<String> getRolesWithMapping(String contactId) throws MultipleOrganisationsException {
    List<LobServiceUserLink> serviceUserLinks = b2cInformation.getServiceUserLinks(contactId);

    if (serviceUserLinks.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> identityEncodedRoles =
        serviceUserLinks.stream()
            .map(this::serviceUserLinkToIdentityEncodedRoleString)
            .distinct()
            .collect(Collectors.toList());

    List<String> allRoles = new ArrayList<>(identityEncodedRoles.size());
    allRoles.addAll(identityEncodedRoles);

    checkUserOnlyBelongsToSingleOrganisation(allRoles);

    return allRoles;
  }
 
  public List<String> getRoles(List<String> roleMappings) {
    List<String> roles = new ArrayList<>();
    for (String roleMapping : roleMappings) {
      String[] b2cRoleFormat = roleMapping.split(":");
      if (b2cRoleFormat.length == 3 && ACTIVE_STATUS_IN_ORGANISATION.equals(b2cRoleFormat[2])) {
        roles.add(b2cRoleFormat[1]);
      }
    }
    return roles;
  }
  
  public String getOrganisationId(List<String> roleMappings) {
    for (String roleMapping : roleMappings) {
      String[] b2cRoleFormat = roleMapping.split(":");
      if (b2cRoleFormat.length == 3 && ACTIVE_STATUS_IN_ORGANISATION.equals(b2cRoleFormat[2])) {
        if ("null".equals(b2cRoleFormat[0])) {
          return null;
        }
        return b2cRoleFormat[0];
      }
    }
    return "";
  }
  
  private void checkUserOnlyBelongsToSingleOrganisation(List<String> allRoles) {

    String firstOrgansiationId = null;
    for (String role : allRoles) {
      String[] b2cRoleFormat = role.split(":");
      if (b2cRoleFormat.length == 3) {
        if (firstOrgansiationId == null && ACTIVE_STATUS_IN_ORGANISATION.equals(b2cRoleFormat[2])) {
          firstOrgansiationId = b2cRoleFormat[0];
        } else if (!StringUtils.equals(firstOrgansiationId, b2cRoleFormat[0])
            && ACTIVE_STATUS_IN_ORGANISATION.equals(b2cRoleFormat[2])) {
          throw new MultipleOrganisationsException();
        }
      }
    }
  }

  private String serviceUserLinkToIdentityEncodedRoleString(LobServiceUserLink link) {
    return link.getOrganisationId()
        + ":"
        + link.getDefraServiceRoleValue()
        + ":"
        + link.getEnrolmentStatus();
  }
}
