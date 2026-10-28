package com.example.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HttpRequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.nanoTime();

        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "UNKNOWN";

        String path = exchange.getRequest().getURI().getRawPath();

        return chain.filter(exchange)
                .doOnError(ex -> log.error(
                        "HTTP {} {} failed", method, path, ex))
                .doFinally(signalType -> {
                    long durationMs =
                            (System.nanoTime() - start) / 1_000_000;

                    HttpStatusCode status =
                            exchange.getResponse().getStatusCode();

                    String statusText = status != null
                            ? String.valueOf(status.value())
                            : "UNKNOWN";

                    if (status != null && status.is5xxServerError()) {
                        log.warn("HTTP {} {} -> {} ({} ms)",
                                method, path, statusText, durationMs);
                    } else {
                        log.info("HTTP {} {} -> {} ({} ms)",
                                method, path, statusText, durationMs);
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}