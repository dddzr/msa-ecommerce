package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 사용자 서비스
                .route("user_route", r -> r.path("/user/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))

                // 상품 서비스
                .route("product_route", r -> r.path("/product/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://product-service"))

                // 주문 서비스
                .route("order_route", r -> r.path("/order/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://order-service"))
                
                .build();
    }
}






