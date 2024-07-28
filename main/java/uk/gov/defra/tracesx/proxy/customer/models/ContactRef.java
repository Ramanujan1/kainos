package uk.gov.defra.tracesx.proxy.customer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ContactRef extends OData {

  @JsonProperty("contactid")
  private String contactId;

  @Builder
  public ContactRef(String contactId, String dynamicsType) {
    this.contactId = contactId;
    super.dynamicsType = dynamicsType;
  }
}
