package uk.gov.defra.tracesx.proxy.customer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

  private String fullname;
  private String firstname;
  private String lastname;

}
