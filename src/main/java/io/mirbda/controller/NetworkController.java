package io.mirbda.controller;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.service.NetworkService;
import io.mirbdacommon.rpc30.response.*;
import io.mirbdacommon.rpc30.request.NodeAddressesRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/network")
@RequiredArgsConstructor
final class NetworkController {
    @PostMapping("connection-count")
    Mono<ConnectionCount> connectionCount() {
        return networkService.connectionCount();
    }

    @PostMapping
    Mono<NetworkInfo> info() {
        return networkService.info();
    }

    @PostMapping("net-totals")
    Mono<NetTotals> netTotals() {
        return networkService.netTotals();
    }

    @PostMapping("list-banned")
    Mono<ListBanned> listBanned() {
        return networkService.listBanned();
    }

    @PostMapping("peers")
    Mono<PeerInfo> peers() {
        return networkService.peerInfo();
    }

    @PostMapping("node-addresses")
    Mono<NodeAddresses> nodeAddresses(@RequestBody(required = false) final NodeAddressesRequest request) {
        return networkService.nodeAddresses(request);
    }

    private final NetworkService networkService;
}
