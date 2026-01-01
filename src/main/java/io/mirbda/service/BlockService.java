package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import io.mirbdacommon.rpc30.response.*;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.request.BlockRequest;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.BlockHashRequest;
import io.mirbdacommon.rpc30.request.BlockStatsRequest;
import io.mirbdacommon.rpc30.request.BlockHeaderRequest;

@Service
@RequiredArgsConstructor
public class BlockService {
    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<?> block(final BlockRequest request) {
        final int verbosity = null == request.verbosity() ? 1 : request.verbosity();

        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) resolveBlockResponseType(verbosity);

        return rpcClient.call(rpcProperty.blockMethod(), List.of(request.blockHash(), verbosity), responseType);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<BlockHash> blockHash(final BlockHashRequest request) {
        return rpcClient.call(rpcProperty.blockHashMethod(), List.of(request.height()), BlockHash.class);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<?> blockHeader(final BlockHeaderRequest request) {
        final Boolean verbose = null == request.verbose() || request.verbose();

        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) resolveBlockHeaderResponseType(verbose);

        return rpcClient.call(rpcProperty.blockHeaderMethod(), List.of(request.blockHash(), verbose), responseType);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<BlockStats> blockStats(final BlockStatsRequest request) {
        Object hashOrHeight = new Object();

        if (null != request.blockHash() && !request.blockHash().isBlank()) {
            hashOrHeight = request.blockHash();
        } else if (null != request.height()) {
            hashOrHeight = request.height();
        }

        final List<Object> params;
        if (null != request.stats() && !request.stats().isEmpty()) {
            params = List.of(hashOrHeight, request.stats().stream().map(BlockStatsRequest.BlockStat::rpcName).toList());
        } else {
            params = List.of(hashOrHeight);
        }

        return rpcClient.call(rpcProperty.blockStatsMethod(), params, BlockStats.class);
    }

    private Class<?> resolveBlockResponseType(final int verbosity) {
        return switch (verbosity) {
            case 0 -> BlockWithV0.class;
            case 1 -> BlockWithV1.class;
            case 2 -> BlockWithV2.class;
            case 3 -> BlockWithV3.class;
            default -> throw new IllegalArgumentException("Unsupported verbosity level: " + verbosity);
        };
    }

    private Class<?> resolveBlockHeaderResponseType(final Boolean verbose) {
        if (verbose == null) {
            throw new IllegalArgumentException("Unsupported verbosity level: null");
        }
        return verbose ? BlockHeaderWithVT.class : BlockHeaderWithVF.class;
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
