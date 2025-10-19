package com.assessment.ragchat.config;

import com.assessment.ragchat.property.ApiProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer{

    private final ApiProperties apiProperties;

    @Bean
    public HandlerInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(apiProperties.getRateLimit().getRequests(), apiProperties.getRateLimit().getDuration());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**"); // Adjust the path pattern as needed
    }
}