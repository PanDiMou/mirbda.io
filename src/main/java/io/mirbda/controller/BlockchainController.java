package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbdacommon.rpc30.response.*;
import io.mirbda.service.BlockchainService;
import io.mirbdacommon.rpc30.request.IndexInfoRequest;
import io.mirbdacommon.rpc30.request.ChainTxStatsRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.mirbdacommon.rpc30.request.DeploymentInfoRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/blockchain")
final class BlockchainController {
    @PostMapping
    Mono<BlockchainInfo> blockchainInfo() {
        return blockchainService.blockchainInfo();
    }

    @PostMapping("/best-block-hash")
    Mono<BestBlockHash> bestBlockHash() {
        return blockchainService.bestBlockHash();
    }

    @PostMapping("block-count")
    Mono<BlockCount> blockCount() {
        return blockchainService.blockCount();
    }

    @PostMapping("chain-tips")
    Mono<ChainTips> chainTips() {
        return blockchainService.chainTips();
    }

    @PostMapping("chain-tx-stats")
    Mono<ChainTxStats> chainTxStats(@Valid @RequestBody final ChainTxStatsRequest request) {
        return blockchainService.chainTxStats(request);
    }

    @PostMapping("deployments")
    Mono<DeploymentInfo> deploymentInfo(@RequestBody(required = false) final DeploymentInfoRequest request) {
        return blockchainService.deploymentInfo(null != request ? request : new DeploymentInfoRequest(null));
    }

    @PostMapping("difficulty")
    Mono<Difficulty> difficulty() {
        return blockchainService.difficulty();
    }

    @PostMapping("index-info")
    Mono<IndexInfo> indexInfo(@RequestBody(required = false) final IndexInfoRequest request) {
        return blockchainService.indexInfo(null != request ? request : new IndexInfoRequest(null));
    }

    private final BlockchainService blockchainService;
}
