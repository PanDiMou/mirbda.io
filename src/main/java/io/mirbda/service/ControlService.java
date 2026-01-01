package io.mirbda.service;

import java.util.List;
import io.mirbda.client.RpcClient;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import io.mirbda.properties.RpcProperty;
import io.mirbdacommon.rpc30.response.Uptime;
import org.springframework.stereotype.Service;
import io.mirbdacommon.rpc30.request.MemoryInfoRequest;
import io.mirbdacommon.rpc30.response.MemoryInfoWithMM;
import io.mirbdacommon.rpc30.response.MemoryInfoWithMS;

@Service
@RequiredArgsConstructor
public class ControlService {
    public Mono<Uptime> uptime() {
        return rpcClient.call(rpcProperty.uptimeMethod(), List.of(), Uptime.class);
    }

    public Mono<?> memoryInfo(final MemoryInfoRequest request) {
        final boolean isMallocinfo = request != null && request.mode() == MemoryInfoRequest.Mode.mallocinfo;

        if (isMallocinfo) {
            return rpcClient.call(rpcProperty.memoryInfoMethod(), List.of(MemoryInfoRequest.Mode.mallocinfo.name()), MemoryInfoWithMM.class);
        }

        final List<Object> params = request != null && request.mode() != null ? List.of(request.mode().name()) : List.of();
        return rpcClient.call(rpcProperty.memoryInfoMethod(), params, MemoryInfoWithMS.class);
    }

    private final RpcClient rpcClient;
    private final RpcProperty rpcProperty;
}
