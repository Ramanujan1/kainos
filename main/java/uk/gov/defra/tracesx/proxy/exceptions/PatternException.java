package uk.gov.defra.tracesx.proxy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PatternException extends RuntimeException {

  public PatternException(Exception exception) {
    super(exception);
  }

  public PatternException(String exception) {
    super(exception);
  }
}
