package io.mirbda.client;

import java.util.List;
import reactor.core.publisher.Mono;
import io.mirbda.request.RpcRequest;
import io.mirbda.properties.RpcProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public final class RpcClient {
    public RpcClient(final WebClient webClient, final RpcProperty rpcProperty) {
        this.webClient = webClient;
        this.rpcVersion = rpcProperty.version();
    }

    public <T> Mono<T> call(final String method, final Class<T> responseType) {
        return call(method, List.of(), responseType);
    }

    public <T> Mono<T> call(final String method, final List<Object> params, final Class<T> responseType) {
        return webClient.post()
                .bodyValue(new RpcRequest(method, rpcVersion, params))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new ResponseStatusException(response.statusCode(), "RPC upstream error: " + body)))
                )
                .bodyToMono(responseType);
    }

    private final String rpcVersion;
    private final WebClient webClient;
}
