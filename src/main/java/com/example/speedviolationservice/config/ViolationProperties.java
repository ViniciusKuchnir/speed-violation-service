package com.example.speedviolationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "violation.tolerance")
public record ViolationProperties(
        int fixedKmh,
        int percentage,
        int percentageTreshold
) {
}
