package com.flightapp.apigateway.filter;

import com.flightapp.apigateway.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Autowired
    private JwtService jwtService;
    
    @Value("${jwt.cookie.name}")
    private String cookieName;
    
    // Public paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = List.of(
        "/auth/",
        "/booked-seats"
    );
    
    public AuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            log.info("AuthenticationFilter: handling {} {}", request.getMethod(), request.getURI());

            // Skip authentication for public paths
            if (isPublicPath(path)) {
                log.info("Skipping authentication for public path: {}", path);
                return chain.filter(exchange);
            }

            HttpCookie cookie = request.getCookies().getFirst(cookieName);
            if (cookie == null) {
                log.warn("No auth cookie found");
                return onError(exchange, "Missing authentication token", HttpStatus.UNAUTHORIZED);
            }

            String token = cookie.getValue();
            log.info("AuthenticationFilter: token = {}", token);

            if (!jwtService.validateToken(token)) {
                log.warn("Invalid or expired token");
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            log.info("AuthenticationFilter: extracted email={}, role={}", email, role);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            log.info("AuthenticationFilter: added headers X-User-Email={}, X-User-Role={}", email, role);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::contains);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        log.error("Authentication error: {}", message);
        return response.setComplete();
    }
    
    public static class Config {
    }
}
