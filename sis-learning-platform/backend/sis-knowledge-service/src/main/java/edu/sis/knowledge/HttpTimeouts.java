package edu.sis.knowledge;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class HttpTimeouts {
  @Bean
  RestClientCustomizer boundedHttp() {
    return builder -> {
      var factory = new SimpleClientHttpRequestFactory();
      factory.setConnectTimeout(5000);
      factory.setReadTimeout(30000);
      String proxy = System.getenv("HTTPS_PROXY");
      if (proxy == null || proxy.isBlank()) proxy = System.getenv("https_proxy");
      if (proxy != null && !proxy.isBlank()) {
        var uri = java.net.URI.create(proxy);
        factory.setProxy(
            new java.net.Proxy(
                java.net.Proxy.Type.HTTP,
                new java.net.InetSocketAddress(
                    uri.getHost(), uri.getPort() > 0 ? uri.getPort() : 80)));
      }
      builder.requestFactory(factory);
    };
  }
}
