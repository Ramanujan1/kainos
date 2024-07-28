package uk.gov.defra.tracesx.proxy.enums;

public enum AuthType {
  B2B("B2B"),
  B2C("B2C");

  private final String value;

  AuthType(final String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
