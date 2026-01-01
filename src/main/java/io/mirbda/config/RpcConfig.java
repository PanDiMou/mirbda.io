package io.mirbda.config;

import java.net.URI;
import java.util.Base64;
import java.time.Duration;
import io.mirbda.properties.RpcProperty;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

@Configuration
@RequiredArgsConstructor
public class RpcConfig {
    @Bean
    public WebClient webClient() {
        final URI uri = URI.create(rpcProperty.baseUrl());

        final HttpClient httpClient = HttpClient.create(
                ConnectionProvider.builder("bitcoin-rpc")
                        .maxConnections(50)
                        .maxIdleTime(Duration.ofSeconds(20))
                        .maxLifeTime(Duration.ofMinutes(10))
                        .evictInBackground(Duration.ofSeconds(30))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .lifo()
                        .build())
                .keepAlive(true)
                .responseTimeout(Duration.ofMinutes(5));

        final WebClient.Builder builder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort())
                .codecs(c -> c.defaultCodecs().maxInMemorySize(128 * 1024 * 1024));

        if (null != uri.getUserInfo()) {
            builder.defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(uri.getUserInfo().getBytes()));
        }

        return builder.build();
    }

    private final RpcProperty rpcProperty;
}
