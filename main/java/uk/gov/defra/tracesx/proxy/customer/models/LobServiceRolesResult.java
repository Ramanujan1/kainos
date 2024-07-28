package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LobServiceRolesResult {

  @JsonProperty("value")
  private List<LobServiceRole> result;

}
