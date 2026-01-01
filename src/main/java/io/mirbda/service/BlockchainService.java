package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import io.mirbda.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import io.mirbdacommon.rpc30.response.*;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.request.IndexInfoRequest;
import org.springframework.cache.annotation.Cacheable;
import io.mirbdacommon.rpc30.request.ChainTxStatsRequest;
import io.mirbdacommon.rpc30.request.DeploymentInfoRequest;

@Service
@RequiredArgsConstructor
public class BlockchainService {
    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "'bestBlockHash'")
    public Mono<BestBlockHash> bestBlockHash() {
        return rpcClient.call(rpcProperty.bestBlockHashMethod(), BestBlockHash.class);
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "'blockchainInfo'")
    public Mono<BlockchainInfo> blockchainInfo() {
        return rpcClient.call(rpcProperty.infoMethod(), BlockchainInfo.class);
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "'blockCount'")
    public Mono<BlockCount> blockCount() {
        return rpcClient.call(rpcProperty.blockCountMethod(), BlockCount.class);
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "'chainTips'")
    public Mono<ChainTips> chainTips() {
        return rpcClient.call(rpcProperty.chainTipsMethod(), ChainTips.class);
    }

    @Cacheable(value = CacheConfig.BLOCK, key = "#request")
    public Mono<ChainTxStats> chainTxStats(final ChainTxStatsRequest request) {
        final int nBlocks = null == request.nBlocks() || request.nBlocks() == 0
                ? 4320
                : request.nBlocks();

        final Mono<String> blockHashMono;

        if (null == request.blockHash() || request.blockHash().isEmpty()) {
            blockHashMono = rpcClient.call(rpcProperty.bestBlockHashMethod(), BestBlockHash.class).map(BestBlockHash::result);
        } else {
            blockHashMono = Mono.just(request.blockHash());
        }

        return blockHashMono.flatMap(blockHash ->
                rpcClient.call(rpcProperty.chainTxStatsMethod(), List.of(nBlocks, blockHash), ChainTxStats.class)
        );
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "#request")
    public Mono<DeploymentInfo> deploymentInfo(final DeploymentInfoRequest request) {
        if (null != request.blockHash() && !request.blockHash().isBlank()) {
            return rpcClient.call(rpcProperty.deploymentInfoMethod(), List.of(request.blockHash()), DeploymentInfo.class);
        }

        return rpcClient.call(rpcProperty.deploymentInfoMethod(), DeploymentInfo.class);
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "'difficulty'")
    public Mono<Difficulty> difficulty() {
        return rpcClient.call(rpcProperty.difficultyMethod(), Difficulty.class);
    }

    @Cacheable(value = CacheConfig.BLOCKCHAIN, key = "#request")
    public Mono<IndexInfo> indexInfo(final IndexInfoRequest request) {
        if (null != request.indexName() && !request.indexName().isBlank()) {
            return rpcClient.call(rpcProperty.indexInfoMethod(), List.of(request.indexName()), IndexInfo.class);
        }

        return rpcClient.call(rpcProperty.indexInfoMethod(), IndexInfo.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
