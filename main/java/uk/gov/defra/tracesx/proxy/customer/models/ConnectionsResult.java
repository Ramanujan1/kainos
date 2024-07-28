package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ConnectionsResult {

  @JsonProperty("value")
  private List<Connection> connections;
}
