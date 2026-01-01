package io.mirbda;

import io.mirbda.properties.RpcProperty;
import io.mirbda.properties.ZmqProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RpcProperty.class, ZmqProperty.class})
public final class MirbdaApi {
    static void main(final String[] args) {
        SpringApplication.run(MirbdaApi.class, args);
    }
}
