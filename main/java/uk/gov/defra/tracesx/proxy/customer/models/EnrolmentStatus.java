package uk.gov.defra.tracesx.proxy.customer.models;

public enum EnrolmentStatus {
  INCOMPLETE(1),
  PENDING(2),
  COMPLETE_APPROVED(3),
  COMPLETE_REJECTED(4);

  private int value;

  EnrolmentStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public String getValueAsString() {
    return Integer.toString(value);
  }
}
