package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Connection {

  @JsonProperty("_record1id_value")
  private String record1IdValue;
  @JsonProperty("_record2id_value")
  private String record2IdValue;
  @JsonProperty("_defra_connectiondetailsid_value")
  private String connectionDetails;
  @JsonProperty("connectionid")
  private String connectionId;
  @JsonProperty("_record1roleid_value")
  private String record1RoleId;

}
