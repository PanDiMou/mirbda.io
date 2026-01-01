package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.response.TestMempoolAccept;
import io.mirbdacommon.rpc30.request.RawTransactionRequest;
import io.mirbdacommon.rpc30.response.RawTransactionWithV0;
import io.mirbdacommon.rpc30.response.DecodeRawTransaction;
import io.mirbdacommon.rpc30.response.RawTransactionWithV2;
import io.mirbdacommon.rpc30.response.RawTransactionWithV1;
import io.mirbdacommon.rpc30.request.TestMempoolAcceptRequest;
import io.mirbdacommon.rpc30.request.DecodeRawTransactionRequest;

@Service
@RequiredArgsConstructor
public class TransactionService {
    @Cacheable(value = CacheConfig.TRANSACTION, key = "#request")
    public Mono<?> rawTransaction(final RawTransactionRequest request) {
        final int verbosity = request.verbosity() != null ? request.verbosity() : 0;

        @SuppressWarnings("unchecked")
        final Class<Object> responseType = (Class<Object>) resolveRawTransactionResponseType(verbosity);

        final List<Object> params = request.blockHash() != null
                ? List.of(request.txid(), verbosity, request.blockHash())
                : List.of(request.txid(), verbosity);

        return rpcClient.call(rpcProperty.rawTransactionMethod(), params, responseType);
    }

    @Cacheable(value = CacheConfig.TRANSACTION, key = "#request")
    public Mono<DecodeRawTransaction> decodeRawTransaction(final DecodeRawTransactionRequest request) {
        final List<Object> params = request.isWitness() != null
                ? List.of(request.hexString(), request.isWitness())
                : List.of(request.hexString());

        return rpcClient.call(rpcProperty.decodeRawTransactionMethod(), params, DecodeRawTransaction.class);
    }

    public Mono<TestMempoolAccept> testMempoolAccept(final TestMempoolAcceptRequest request) {
        final List<Object> params = request.maxFeeRate() != null
                ? List.of(request.rawtxs(), request.maxFeeRate())
                : List.of(request.rawtxs());

        return rpcClient.call(rpcProperty.testMempoolAcceptMethod(), params, TestMempoolAccept.class);
    }

    private Class<?> resolveRawTransactionResponseType(final int verbosity) {
        if (verbosity == 2) {
            return RawTransactionWithV2.class;
        }

        if (verbosity == 1) {
            return RawTransactionWithV1.class;
        }

        return RawTransactionWithV0.class;
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
