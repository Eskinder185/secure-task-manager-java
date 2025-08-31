package com.esecure.securetask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.ratelimit.windowSec:600}")
    private int windowSec; // default 10 minutes
    @Value("${app.ratelimit.max:10}")
    private int max; // default 10 requests per window

    private static class Counter {
        int count;
        long windowStart;
    }
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (request.getMethod().equalsIgnoreCase("POST") &&
            (path.equals("/api/issues") || path.equals("/api/issues/simple"))) {

            String ip = clientIp(request);
            long now = Instant.now().getEpochSecond();
            Counter c = counters.computeIfAbsent(ip, k -> {
                Counter x = new Counter();
                x.count = 0;
                x.windowStart = now;
                return x;
            });

            synchronized (c) {
                if (now - c.windowStart >= windowSec) {
                    c.count = 0;
                    c.windowStart = now;
                }
                c.count++;
                if (c.count > max) {
                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write("{"error":"rate_limited","retryAfterSec":" + (windowSec - (now - c.windowStart)) + "}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
