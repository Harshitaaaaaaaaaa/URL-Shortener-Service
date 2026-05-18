package com.harshita.urlshortner.service;

import com.harshita.urlshortner.entity.UrlMapping;
import com.harshita.urlshortner.exception.ShortUrlNotFoundException;
import com.harshita.urlshortner.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.harshita.urlshortner.utils.Base62Encoder;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    private final RedisTemplate<String, String> redisTemplate;

    public UrlMapping shortenUrl(String originalUrl) {

        UrlMapping mapping = new UrlMapping();

        mapping.setOriginalUrl(originalUrl);

        mapping.setShortCode("temp");

        mapping = repository.save(mapping);

        String shortCode = Base62Encoder.encode(mapping.getId());

        mapping.setShortCode(shortCode);

        return repository.save(mapping);
    }

    public UrlMapping getOriginalUrl(String shortCode) {

        String cachedUrl = redisTemplate.opsForValue().get(shortCode);

        if (cachedUrl != null) {

            System.out.println("Cache HIT");

            UrlMapping mapping = new UrlMapping();

            mapping.setOriginalUrl(cachedUrl);

            return mapping;
        }

        System.out.println("Cache MISS");

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

        redisTemplate.opsForValue()
                .set(shortCode, mapping.getOriginalUrl());

        return mapping;
    }
}