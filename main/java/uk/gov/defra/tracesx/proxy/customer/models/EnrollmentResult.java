package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentResult {

  @JsonProperty("defra_lobserviceuserlinkid")
  private String lobServiceUserLinkId;

}
