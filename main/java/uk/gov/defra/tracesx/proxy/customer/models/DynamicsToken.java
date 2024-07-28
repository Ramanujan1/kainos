package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DynamicsToken {

  private String tokenType;
  private Long expiresIn;
  private Long extExpiresIn;
  private Long expiresOn;
  private Long notBefore;
  private String resource;
  private String accessToken;

}
