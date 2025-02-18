// 사용 x
package com.example.gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.gateway.util.JwtTokenProvider;

import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GatewayFilter {
    /* 사용 안 함. 대신 GatewayFilterFactory 사용함.*/
    /*
        특성               | JwtGatewayFilter                      | GatewayFilterFactory
        ------------------|---------------------------------------|-----------------------------------------
        설정 방식          | 생성자 주입으로 필터의 동작을 설정      | Config 클래스를 통해 동적으로 설정 가능
        동적 파라미터       | 생성자에서 정적 값으로 설정            | apply 메서드에서 동적으로 파라미터 설정
        유연성             | 정적인 값에 의존                       | 다양한 설정값을 동적으로 처리 가능
        주 용도            | 고정된 동작을 하는 필터 구현            | 재사용 가능하고 동적으로 파라미터를 처리하는 필터
    */

    private final JwtTokenProvider jwtTokenProvider;

    public JwtGatewayFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractTokenFromRequest(exchange);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            List<String> roles = jwtTokenProvider.getRolesFromToken(token);
            
        // 권한 체크
        // if (requiredRole  != null && !roles.contains(requiredRole)) {
        //     //log.info("권한 부족: 필요한 역할 = {}, 사용자의 역할 = {}", requiredRole, roles);
        //     exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        //     return exchange.getResponse().setComplete();
        // }
        
        /* 사용자 정보 저장 */

        // 1.  속성 맵에 추가: gateway 내 요청-응답 흐름 내의 로직에서만 사용
        // exchange.getAttributes().put("username", username);
        // exchange.getAttributes().put("roles", roles);

        // 2. 헤더 추가: 하위 서비스나 외부 API에도 반영
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", username)
                .header("X-User-Roles", String.join(", ", roles))
                .build();

        exchange = exchange.mutate().request(modifiedRequest).build();

        // 3. SecurityContext에 설정: gateWay쪽 springSecurity만 적용되는거라 사용자 서비스랑 연동x
        // List<GrantedAuthority> authorities = roles.stream()
        //         .map(role -> new SimpleGrantedAuthority("ROLE_" + role))  // ROLE_ prefix 추가
        //         .collect(Collectors.toList());
        
        // Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        // SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String extractTokenFromRequest(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
