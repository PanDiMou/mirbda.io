package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.response.DecodeScript;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.response.ValidateAddress;
import io.mirbdacommon.rpc30.response.EstimateSmartFee;
import io.mirbdacommon.rpc30.request.DecodeScriptRequest;
import io.mirbdacommon.rpc30.request.ValidateAddressRequest;
import io.mirbdacommon.rpc30.request.EstimationSmartFeeRequest;

@Service
@RequiredArgsConstructor
public class UtilService {
    @Cacheable(value = CacheConfig.MINING, key = "#request")
    public Mono<EstimateSmartFee> estimateSmartFee(final EstimationSmartFeeRequest request) {
        final EstimationSmartFeeRequest.EstimationMode estimationMode = null == request.estimationMode()
                ? EstimationSmartFeeRequest.EstimationMode.CONSERVATIVE
                : request.estimationMode();

        return rpcClient.call(rpcProperty.estimateSmartFeeMethod(), List.of(request.confTarget(), estimationMode.name()), EstimateSmartFee.class);
    }

    @Cacheable(value = CacheConfig.MINING, key = "#request")
    public Mono<ValidateAddress> validateAddress(final ValidateAddressRequest request) {
        return rpcClient.call(rpcProperty.validateAddressMethod(), List.of(request.address()), ValidateAddress.class);
    }

    @Cacheable(value = CacheConfig.MINING, key = "#request")
    public Mono<DecodeScript> decodeScript(final DecodeScriptRequest request) {
        return rpcClient.call(rpcProperty.decodeScriptMethod(), List.of(request.hexstring()), DecodeScript.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
