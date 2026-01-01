package io.mirbda.controller;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.service.MiningService;
import io.mirbdacommon.rpc30.response.MiningInfo;
import io.mirbdacommon.rpc30.response.NetworkHashPs;
import io.mirbdacommon.rpc30.request.NetworkHashPsRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/mining")
@RequiredArgsConstructor
final class MiningController {
    @PostMapping
    Mono<MiningInfo> info() {
        return miningService.miningInfo();
    }

    @PostMapping("network-hash-ps")
    Mono<NetworkHashPs> networkHashPs(@RequestBody(required = false) final NetworkHashPsRequest request) {
        return miningService.networkHashPs(request);
    }

    private final MiningService miningService;
}
