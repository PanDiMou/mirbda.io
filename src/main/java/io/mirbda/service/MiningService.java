package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.response.MiningInfo;
import io.mirbdacommon.rpc30.response.NetworkHashPs;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.NetworkHashPsRequest;

@Service
@RequiredArgsConstructor
public class MiningService {
    @Cacheable(value = CacheConfig.MINING, key = "'miningInfo'")
    public Mono<MiningInfo> miningInfo() {
        return rpcClient.call(rpcProperty.miningInfoMethod(), List.of(), MiningInfo.class);
    }

    @Cacheable(value = CacheConfig.MINING, key = "#request != null ? #request : 'networkHashPs'")
    public Mono<NetworkHashPs> networkHashPs(final NetworkHashPsRequest request) {
        if (request == null || (request.nblocks() == null && request.height() == null)) {
            return rpcClient.call(rpcProperty.networkHashPsMethod(), List.of(), NetworkHashPs.class);
        }

        if (request.height() != null && request.nblocks() == null) {
            return rpcClient.call(rpcProperty.networkHashPsMethod(), List.of(120, request.height()), NetworkHashPs.class);
        }

        if (request.height() == null) {
            return rpcClient.call(rpcProperty.networkHashPsMethod(), List.of(request.nblocks()), NetworkHashPs.class);
        }

        return rpcClient.call(rpcProperty.networkHashPsMethod(), List.of(request.nblocks(), request.height()), NetworkHashPs.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
