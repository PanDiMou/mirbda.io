package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import io.mirbdacommon.rpc30.response.*;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.RawMempoolRequest;
import io.mirbdacommon.rpc30.request.MempoolEntryRequest;
import io.mirbdacommon.rpc30.request.MempoolAncestorsRequest;
import io.mirbdacommon.rpc30.request.MempoolDescendantsRequest;

@Service
@RequiredArgsConstructor
public class MempoolService {
    @Cacheable(value = CacheConfig.MEMPOOL, key = "'mempoolInfo'")
    public Mono<MempoolInfo> mempoolInfo() {
        return rpcClient.call(rpcProperty.mempoolInfoMethod(), MempoolInfo.class);
    }

    @Cacheable(value = CacheConfig.MEMPOOL, key = "#request")
    public Mono<MempoolEntry> mempoolEntry(final MempoolEntryRequest request) {
        return rpcClient.call(rpcProperty.mempoolEntryMethod(), List.of(request.txid()), MempoolEntry.class);
    }

    @Cacheable(value = CacheConfig.MEMPOOL, key = "#request")
    public Mono<?> rawMempool(final RawMempoolRequest request) {
        final Boolean verbose = Boolean.TRUE.equals(request.verbose());
        final Boolean mempoolSequence = Boolean.TRUE.equals(request.mempoolSequence());

        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) resolveRawMempoolResponseType(verbose, mempoolSequence);

        return rpcClient.call(rpcProperty.rawMempoolMethod(), List.of(verbose, mempoolSequence), responseType);
    }

    @Cacheable(value = CacheConfig.MEMPOOL, key = "#request")
    public Mono<?> mempoolAncestors(final MempoolAncestorsRequest request) {
        final Boolean verbose = Boolean.TRUE.equals(request.verbose());

        final Class<?> type = verbose ? MempoolAncestorsWithVT.class : MempoolAncestorsWithVF.class;
        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) type;

        return rpcClient.call(rpcProperty.mempoolAncestorsMethod(), List.of(request.txid(), verbose), responseType);
    }

    @Cacheable(value = CacheConfig.MEMPOOL, key = "#request")
    public Mono<?> mempoolDescendants(final MempoolDescendantsRequest request) {
        final Boolean verbose = Boolean.TRUE.equals(request.verbose());

        final Class<?> type = verbose ? MempoolDescendantsWithVT.class : MempoolDescendantsWithVF.class;
        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) type;

        return rpcClient.call(rpcProperty.mempoolDescendantsMethod(), List.of(request.txid(), verbose), responseType);
    }

    //
    private Class<?> resolveRawMempoolResponseType(final Boolean verbose, final Boolean mempoolSequence) {
        if (!verbose && mempoolSequence) {
            return RawMempoolWithVFAndMT.class;
        }

        if (verbose) {
            return RawMempoolWithVT.class;
        }

        return RawMempoolWithVF.class;
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
