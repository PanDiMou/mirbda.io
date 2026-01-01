package io.mirbda.controller;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.service.ControlService;
import io.mirbdacommon.rpc30.response.Uptime;
import io.mirbdacommon.rpc30.request.MemoryInfoRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/control")
@RequiredArgsConstructor
final class ControlController {
    @PostMapping("uptime")
    Mono<Uptime> uptime() {
        return controlService.uptime();
    }

    @PostMapping("memory-info")
    Mono<?> memoryInfo(@RequestBody(required = false) final MemoryInfoRequest request) {
        return controlService.memoryInfo(request);
    }

    private final ControlService controlService;
}
