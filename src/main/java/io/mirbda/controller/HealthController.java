package io.mirbda.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
final class HealthController {
    @GetMapping("/health")
    Mono<Void> health() {
        return Mono.empty();
    }
}
