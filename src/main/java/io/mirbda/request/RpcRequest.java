package io.mirbda.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RpcRequest(
        @JsonProperty("method") String method,
        @JsonProperty("jsonrpc") String jsonRpc,
        @JsonProperty("params") List<Object> parameters
) { }
