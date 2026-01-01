package io.mirbda.controller;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import io.mirbda.service.UtilService;
import lombok.RequiredArgsConstructor;
import io.mirbdacommon.rpc30.response.DecodeScript;
import io.mirbdacommon.rpc30.response.ValidateAddress;
import io.mirbdacommon.rpc30.response.EstimateSmartFee;
import io.mirbdacommon.rpc30.request.DecodeScriptRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.mirbdacommon.rpc30.request.ValidateAddressRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.mirbdacommon.rpc30.request.EstimationSmartFeeRequest;

@RestController
@RequestMapping("v1/util")
@RequiredArgsConstructor
final class UtilController {
    @PostMapping("estimate-smart-fee")
    Mono<EstimateSmartFee> estimateSmartFee(@Valid @RequestBody final EstimationSmartFeeRequest request) {
        return utilService.estimateSmartFee(request);
    }

    @PostMapping("validate-address")
    Mono<ValidateAddress> validateAddress(@Valid @RequestBody final ValidateAddressRequest request) {
        return utilService.validateAddress(request);
    }

    @PostMapping("decode-script")
    Mono<DecodeScript> decodeScript(@Valid @RequestBody final DecodeScriptRequest request) {
        return utilService.decodeScript(request);
    }

    private final UtilService utilService;
}
