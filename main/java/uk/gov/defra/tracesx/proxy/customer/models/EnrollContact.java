package uk.gov.defra.tracesx.proxy.customer.models;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class EnrollContact {
  @JsonProperty("defra_connectiondetail@odata.bind")
  private final String conntectionDetail;
  @JsonProperty("defra_ServiceUser@odata.bind")
  private final String serviceUser;
  @JsonProperty("defra_ServiceRole@odata.bind")
  private final String serviceRole;
  @JsonInclude(NON_NULL)
  @JsonProperty("defra_Organisation@odata.bind")
  private final String organisation;
  @JsonProperty("defra_enrolmentstatus")
  private final int enrolmentStatus;

}
