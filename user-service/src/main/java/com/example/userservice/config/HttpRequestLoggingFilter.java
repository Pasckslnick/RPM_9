package com.example.userservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.nanoTime();
        String method = request.getMethod();
        String path = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.error("HTTP {} {} failed after {} ms",
                    method, path, durationMs, ex);
            throw ex;
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            int status = response.getStatus();

            if (status >= 500) {
                log.warn("HTTP {} {} -> {} ({} ms)",
                        method, path, status, durationMs);
            } else {
                log.info("HTTP {} {} -> {} ({} ms)",
                        method, path, status, durationMs);
            }
        }
    }
}