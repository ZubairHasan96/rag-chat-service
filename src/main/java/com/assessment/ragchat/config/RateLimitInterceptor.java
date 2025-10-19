package com.assessment.ragchat.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    // Simple in-memory store: use Redis or distributed store for production
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long apiRateLimitRequests;
    private final long apiRateLimitDuration;

    private Bucket newBucket() {
        Refill refill = Refill.intervally(apiRateLimitRequests, Duration.ofMinutes(apiRateLimitDuration));
        Bandwidth limit = Bandwidth.classic(apiRateLimitRequests, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> newBucket());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // Rate limit by API key if present, otherwise by client IP
        String key = request.getHeader("x-api-key");
        if (key == null || key.isBlank()) {
            key = request.getRemoteAddr();
        }

        Bucket bucket = resolveBucket(key);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefillMillis = probe.getNanosToWaitForRefill() / 1_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", Long.toString(Duration.ofMillis(waitForRefillMillis).getSeconds()));
            response.getWriter().write("Too Many Requests");
            return false;
        }
    }
}