package com.assessment.ragchat.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiProperties {

    private String key;
    private RateLimit rateLimit = new RateLimit();

    @Getter
    @Setter
    public static class RateLimit {
        private long requests = 100;
        private long duration = 1; // in minutes
    }
}