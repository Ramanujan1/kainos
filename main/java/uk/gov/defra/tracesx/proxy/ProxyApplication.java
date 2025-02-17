package uk.gov.defra.tracesx.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ProxyApplication {
  public static void main(String[] args) {
    SpringApplication.run(ProxyApplication.class, args);
  }
}
