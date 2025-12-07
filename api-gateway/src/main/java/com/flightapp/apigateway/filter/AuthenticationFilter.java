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

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Autowired
    private JwtService jwtService;
    
    @Value("${jwt.cookie.name}")
    private String cookieName;
    
    public AuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            log.debug("Processing request: {} {}", request.getMethod(), request.getURI());
            
            HttpCookie cookie = request.getCookies().getFirst(cookieName);
            
            if (cookie == null) {
                log.warn("No auth cookie found in request");
                return onError(exchange, "Missing authentication token", HttpStatus.UNAUTHORIZED);
            }
            
            String token = cookie.getValue();
            
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid or expired token");
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
            
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            
            log.info("Authenticated user: {} with role: {}", email, role);
            
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
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
