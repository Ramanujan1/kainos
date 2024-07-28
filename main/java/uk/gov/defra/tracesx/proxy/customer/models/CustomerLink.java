package uk.gov.defra.tracesx.proxy.customer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CustomerLink {

  private String connectionId;
  private String connectionDetailsId;
  private String organisationId;
  private String customerRoleId;

}