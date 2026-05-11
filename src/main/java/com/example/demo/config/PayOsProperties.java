package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "payos")
public class PayOsProperties {
    private String clientId;
    private String apiKey;
    private String checksumKey;

}
