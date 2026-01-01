package io.mirbda.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ZmqProperty(
        String hashtxUrl,
        String hashblockUrl
) { }
