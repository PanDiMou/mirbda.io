package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import io.mirbda.service.UtxoService;
import lombok.RequiredArgsConstructor;
import io.mirbdacommon.rpc30.response.TxOut;
import io.mirbdacommon.rpc30.response.TxOutProof;
import io.mirbdacommon.rpc30.request.TxOutRequest;
import io.mirbdacommon.rpc30.response.TxOutSetInfo;
import io.mirbdacommon.rpc30.response.VerifyTxOutProof;
import io.mirbdacommon.rpc30.request.TxOutProofRequest;
import io.mirbdacommon.rpc30.request.TxOutSetInfoRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.mirbdacommon.rpc30.request.VerifyTxOutProofRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/utxo")
@RequiredArgsConstructor
final class UtxoController {
    @PostMapping
    Mono<TxOut> txOut(@Valid @RequestBody final TxOutRequest request) {
        return utxoService.txOut(request);
    }

    @PostMapping("proof")
    Mono<TxOutProof> txOutProof(@Valid @RequestBody final TxOutProofRequest request) {
        return utxoService.txOutProof(request);
    }

    @PostMapping("verify-proof")
    Mono<VerifyTxOutProof> verifyTxOutProof(@Valid @RequestBody final VerifyTxOutProofRequest request) {
        return utxoService.verifyTxOutProof(request);
    }

    @PostMapping("set-info")
    Mono<TxOutSetInfo> txOutSetInfo(@Valid @RequestBody final TxOutSetInfoRequest request) {
        return utxoService.txOutSetInfo(request);
    }

    private final UtxoService utxoService;
}
