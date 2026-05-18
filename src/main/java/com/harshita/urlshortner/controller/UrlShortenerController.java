package com.harshita.urlshortner.controller;

import com.harshita.urlshortner.dto.UrlRequest;
import com.harshita.urlshortner.dto.UrlResponse;
import com.harshita.urlshortner.entity.UrlMapping;
import com.harshita.urlshortner.service.RateLimitingService;
import com.harshita.urlshortner.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService service;

    private final RateLimitingService rateLimitingService;

    @PostMapping("/shorten")
    public UrlResponse shortenUrl(
            @Valid @RequestBody UrlRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        rateLimitingService.validateRateLimit(ipAddress);

        UrlMapping mapping = service.shortenUrl(request.getUrl());

        String shortUrl = "http://localhost:8080/api/url/"
                + mapping.getShortCode();

        return new UrlResponse(
                mapping.getId(),
                mapping.getOriginalUrl(),
                mapping.getShortCode(),
                shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        UrlMapping mapping = service.getOriginalUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                "Location",
                mapping.getOriginalUrl());

        return new ResponseEntity<>(
                headers,
                HttpStatus.FOUND);
    }
}