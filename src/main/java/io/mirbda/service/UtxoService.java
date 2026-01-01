package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import io.mirbdacommon.rpc30.response.TxOut;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.response.TxOutProof;
import io.mirbdacommon.rpc30.request.TxOutRequest;
import io.mirbdacommon.rpc30.response.TxOutSetInfo;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.TxOutProofRequest;
import io.mirbdacommon.rpc30.response.VerifyTxOutProof;
import io.mirbdacommon.rpc30.request.TxOutSetInfoRequest;
import io.mirbdacommon.rpc30.request.VerifyTxOutProofRequest;

@Service
@RequiredArgsConstructor
public class UtxoService {
    @Cacheable(value = CacheConfig.UTXO, key = "#request")
    public Mono<TxOut> txOut(final TxOutRequest request) {
        final Boolean includeMempool = null == request.includeMempool() || request.includeMempool();

        return rpcClient.call(rpcProperty.txOutMethod(), List.of(request.txid(), request.n(), includeMempool), TxOut.class);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<TxOutProof> txOutProof(final TxOutProofRequest request) {
        final List<Object> params = null == request.blockHash() || request.blockHash().isEmpty()
                ? List.of(request.txids())
                : List.of(request.txids(), request.blockHash());

        return rpcClient.call(rpcProperty.txOutProofMethod(), params, TxOutProof.class);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<VerifyTxOutProof> verifyTxOutProof(final VerifyTxOutProofRequest request) {
        return rpcClient.call(rpcProperty.verifyTxOutProofMethod(), List.of(request.proof()), VerifyTxOutProof.class);
    }

    @Cacheable(value = CacheConfig.UTXO, key = "#request")
    public Mono<TxOutSetInfo> txOutSetInfo(final TxOutSetInfoRequest request) {
        final String hashType = null == request.hashType() || request.hashType().isEmpty()
                ? "hash_serialized_3"
                : request.hashType();

        final List<Object> params = new java.util.ArrayList<>();
        params.add(hashType);

        if (request.hashOrHeight() != null) {
            try {
                params.add(Long.parseLong(request.hashOrHeight()));
            } catch (final NumberFormatException e) {
                params.add(request.hashOrHeight());
            }
        }

        if (request.hashOrHeight() != null && request.useIndex() != null) {
            params.add(request.useIndex());
        }

        return rpcClient.call(rpcProperty.txOutSetInfoMethod(), params, TxOutSetInfo.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
