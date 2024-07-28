package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Wither
@EqualsAndHashCode
@ToString
public class LobServiceUserLink {

  @JsonProperty("_defra_servicerole_value")
  private String defraServiceRoleValue;

  @JsonProperty("_defra_organisation_value")
  private String organisationId;

  @JsonProperty("defra_enrolmentstatus")
  private String enrolmentStatus;

  @JsonProperty("statecode")
  private int statecode;

  @JsonProperty("statuscode")
  private int statuscode;
}
