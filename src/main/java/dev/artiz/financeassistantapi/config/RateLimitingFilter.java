package dev.artiz.financeassistantapi.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RateLimitingFilter implements WebFilter {

    private static final String LIMIT_RESPONSE =
        "{\"error\": \"Too many requests. Please slow down. Prediction is expensive!\"}";

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        return Bucket.builder()
            .addLimit(
                Bandwidth.classic(
                    5,
                    Refill.intervally(5, Duration.ofMinutes(1))
                )
            )
            .build();
    }

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        WebFilterChain chain
    ) {
        String path = exchange.getRequest().getPath().pathWithinApplication()
            .value();

        if (!path.startsWith("/api/v1/predictions")) {
            return chain.filter(exchange);
        }

        String key = Optional
            .ofNullable(exchange.getRequest().getRemoteAddress())
            .map(address ->
                address.getAddress() != null
                    ? address.getAddress().getHostAddress()
                    : address.getHostString()
            )
            .orElse("unknown");
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        }

        return sendErrorResponse(exchange);
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = response
            .bufferFactory()
            .wrap(LIMIT_RESPONSE.getBytes());

        return response.writeWith(Mono.just(buffer));
    }
}
