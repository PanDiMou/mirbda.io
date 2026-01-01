package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbdacommon.rpc30.response.*;
import io.mirbda.properties.RpcProperty;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.NodeAddressesRequest;

@Service
@RequiredArgsConstructor
public class NetworkService {
    @Cacheable(value = CacheConfig.NETWORK, key = "'connectionCount'")
    public Mono<ConnectionCount> connectionCount() {
        return rpcClient.call(rpcProperty.connectionCountMethod(), List.of(), ConnectionCount.class);
    }

    @Cacheable(value = CacheConfig.NETWORK, key = "'networkInfo'")
    public Mono<NetworkInfo> info() {
        return rpcClient.call(rpcProperty.networkInfoMethod(), List.of(), NetworkInfo.class);
    }

    @Cacheable(value = CacheConfig.NETWORK, key = "'netTotals'")
    public Mono<NetTotals> netTotals() {
        return rpcClient.call(rpcProperty.netTotalsMethod(), List.of(), NetTotals.class);
    }

    @Cacheable(value = CacheConfig.NETWORK, key = "'listBanned'")
    public Mono<ListBanned> listBanned() {
        return rpcClient.call(rpcProperty.listBannedMethod(), ListBanned.class);
    }

    @Cacheable(value = CacheConfig.NETWORK, key = "'peerInfo'")
    public Mono<PeerInfo> peerInfo() {
        return rpcClient.call(rpcProperty.peerInfoMethod(), PeerInfo.class);
    }

    @Cacheable(value = CacheConfig.NETWORK, key = "#request != null ? #request : 'nodeAddresses'")
    public Mono<NodeAddresses> nodeAddresses(final NodeAddressesRequest request) {
        if (request == null || (request.count() == null && request.network() == null)) {
            return rpcClient.call(rpcProperty.nodeAddressesMethod(), List.of(), NodeAddresses.class);
        }

        if (request.network() != null && request.count() == null) {
            return rpcClient.call(rpcProperty.nodeAddressesMethod(), List.of(1, request.network()), NodeAddresses.class);
        }

        if (request.network() == null) {
            return rpcClient.call(rpcProperty.nodeAddressesMethod(), List.of(request.count()), NodeAddresses.class);
        }

        return rpcClient.call(rpcProperty.nodeAddressesMethod(), List.of(request.count(), request.network()), NodeAddresses.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
