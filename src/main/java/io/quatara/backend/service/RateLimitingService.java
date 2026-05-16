package io.quatara.backend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitingService {

    @Value("${app.rate-limit.requests-per-minute:60}")
    private int requestPerMinute;

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    public Bucket resolveBucket(String token) {
        return cache.get(token, this::createBucket);
    }

    private Bucket createBucket(String token) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(requestPerMinute)
                        .refillIntervally(requestPerMinute, Duration.ofMinutes(1)))
                .build();
    }
}
