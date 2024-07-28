package uk.gov.defra.tracesx.proxy.exceptions;

public class DecodeException extends Exception {

  public DecodeException(Exception exception) {
    super(exception);
  }
}