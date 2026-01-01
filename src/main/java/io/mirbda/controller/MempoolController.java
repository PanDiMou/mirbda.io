package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.service.MempoolService;
import io.mirbdacommon.rpc30.response.MempoolInfo;
import io.mirbdacommon.rpc30.response.MempoolEntry;
import io.mirbdacommon.rpc30.request.RawMempoolRequest;
import io.mirbdacommon.rpc30.request.MempoolEntryRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.mirbdacommon.rpc30.request.MempoolAncestorsRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.mirbdacommon.rpc30.request.MempoolDescendantsRequest;

@RestController
@RequestMapping("v1/mempool")
@RequiredArgsConstructor
final class MempoolController {
    @PostMapping
    Mono<MempoolInfo> mempoolInfo() {
        return mempoolService.mempoolInfo();
    }

    @PostMapping("entry")
    Mono<MempoolEntry> mempoolEntry(@Valid @RequestBody final MempoolEntryRequest request) {
        return mempoolService.mempoolEntry(request);
    }

    @PostMapping("raw")
    Mono<?> rawMempool(@Valid @RequestBody final RawMempoolRequest request) {
        return mempoolService.rawMempool(request);
    }

    @PostMapping("ancestors")
    Mono<?> mempoolAncestors(@Valid @RequestBody final MempoolAncestorsRequest request) {
        return mempoolService.mempoolAncestors(request);
    }

    @PostMapping("descendants")
    Mono<?> mempoolDescendants(@Valid @RequestBody final MempoolDescendantsRequest request) {
        return mempoolService.mempoolDescendants(request);
    }

    private final MempoolService mempoolService;
}
