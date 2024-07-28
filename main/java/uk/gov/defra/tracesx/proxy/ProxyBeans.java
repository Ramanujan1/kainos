package uk.gov.defra.tracesx.proxy;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Configuration
public class ProxyBeans {

  private ProxyConfiguration proxyConfiguration;

  @Autowired
  public ProxyBeans(ProxyConfiguration proxyConfiguration) {
    this.proxyConfiguration = proxyConfiguration;
  }

  @Bean
  public RestTemplate httpClient() {
    return new RestTemplate();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public JwkProvider jwkProviderAd() throws MalformedURLException {
    UrlJwkProvider urlJwkProvider = new UrlJwkProvider(new URL(proxyConfiguration.getJwkUrl()));
    return new GuavaCachedJwkProvider(urlJwkProvider, 10, 1, TimeUnit.HOURS);
  }

  @Bean
  public JwkProvider jwkProviderB2c() throws MalformedURLException {
    return new GuavaCachedJwkProvider(
        new UrlJwkProvider(
            new URL(proxyConfiguration.getJwkUrlExternal())),
            10,
            1,
            TimeUnit.HOURS);
  }
}
