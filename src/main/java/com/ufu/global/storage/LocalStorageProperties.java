package com.ufu.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record LocalStorageProperties(String uploadDir) {
}
