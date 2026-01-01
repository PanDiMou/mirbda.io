package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import io.mirbda.service.BlockService;
import lombok.RequiredArgsConstructor;
import io.mirbdacommon.rpc30.response.BlockHash;
import io.mirbdacommon.rpc30.response.BlockStats;
import io.mirbdacommon.rpc30.request.BlockRequest;
import io.mirbdacommon.rpc30.request.BlockHashRequest;
import io.mirbdacommon.rpc30.request.BlockStatsRequest;
import io.mirbdacommon.rpc30.request.BlockHeaderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/block")
@RequiredArgsConstructor
final class BlockController {
    @PostMapping
    Mono<?> block(@Valid @RequestBody final BlockRequest request) {
        return blockService.block(request);
    }

    @PostMapping("hash")
    Mono<BlockHash> blockHash(@Valid @RequestBody final BlockHashRequest request) {
        return blockService.blockHash(request);
    }

    @PostMapping("header")
    Mono<?> blockHeader(@Valid @RequestBody final BlockHeaderRequest request) {
        return blockService.blockHeader(request);
    }

    @PostMapping("stats")
    Mono<BlockStats> blockStats(@Valid @RequestBody final BlockStatsRequest request) {
        return blockService.blockStats(request);
    }

    private final BlockService blockService;
}
