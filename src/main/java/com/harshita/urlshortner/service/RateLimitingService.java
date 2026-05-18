package com.harshita.urlshortner.service;

import com.harshita.urlshortner.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;

    public void validateRateLimit(String ipAddress) {

        String key = "rate_limit:" + ipAddress;

        String currentCount = redisTemplate.opsForValue().get(key);

        int requestCount = currentCount != null
                ? Integer.parseInt(currentCount)
                : 0;

        if (requestCount >= MAX_REQUESTS) {

            throw new RateLimitExceededException(
                    "Too many requests. Try again later.");
        }

        redisTemplate.opsForValue()
                .increment(key);

        redisTemplate.expire(key, 1, TimeUnit.MINUTES);
    }
}