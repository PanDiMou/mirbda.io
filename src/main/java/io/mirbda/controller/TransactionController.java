package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.service.TransactionService;
import io.mirbdacommon.rpc30.response.TestMempoolAccept;
import io.mirbdacommon.rpc30.response.DecodeRawTransaction;
import io.mirbdacommon.rpc30.request.RawTransactionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.mirbdacommon.rpc30.request.TestMempoolAcceptRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.mirbdacommon.rpc30.request.DecodeRawTransactionRequest;

@RestController
@RequestMapping("/v1/transaction")
@RequiredArgsConstructor
final class TransactionController {
    @PostMapping("raw")
    Mono<?> rawTransaction(@Valid @RequestBody final RawTransactionRequest request) {
        return transactionService.rawTransaction(request);
    }

    @PostMapping("decode-raw")
    Mono<DecodeRawTransaction> decodeRawTransaction(@Valid @RequestBody final DecodeRawTransactionRequest request) {
        return transactionService.decodeRawTransaction(request);
    }

    @PostMapping("test-mempool-accept")
    Mono<TestMempoolAccept> testMempoolAccept(@Valid @RequestBody final TestMempoolAcceptRequest request) {
        return transactionService.testMempoolAccept(request);
    }

    private final TransactionService transactionService;
}
