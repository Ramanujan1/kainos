package uk.gov.defra.tracesx.proxy.customer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEnrollmentData {

  private String lobServiceRoleId;
  private String customerId;
  private String connectionDetailsId;
  private String organisation;
}

