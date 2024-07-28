package uk.gov.defra.tracesx.proxy.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootResource {

  // The purpose of this class is to prevent spurious logging of BadCredentialExceptions
  // when deployed to Azure

  @RequestMapping(method = RequestMethod.GET)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity defaultGet() {
    return new ResponseEntity(HttpStatus.OK);
  }
}
