package uk.gov.defra.tracesx.proxy.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.tracesx.proxy.security.jwt.JwtKey;
import uk.gov.defra.tracesx.proxy.wellknown.WellKnownConfiguration;

@RestController
public class WellKnownResource {

  private final WellKnownConfiguration wellKnownConfiguration;
  private final JwtKey jwtKey;

  @Autowired
  public WellKnownResource(WellKnownConfiguration wellKnownConfiguration, JwtKey jwtKey) {
    this.wellKnownConfiguration = wellKnownConfiguration;
    this.jwtKey = jwtKey;
  }

  @GetMapping(value = "/.well-known/openid-configuration")
  public ResponseEntity configuration() {
    return ResponseEntity.ok(wellKnownConfiguration);
  }

  @GetMapping(value = "/common/discovery/keys")
  public ResponseEntity keys() {
    return ResponseEntity.ok(jwtKey.getWellKnownKeys());
  }
}
