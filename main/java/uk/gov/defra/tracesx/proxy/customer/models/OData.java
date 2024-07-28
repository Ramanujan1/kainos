package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OData {

  @JsonProperty("@odata.type")
  protected String dynamicsType;
}
