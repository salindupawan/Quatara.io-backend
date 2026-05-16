package io.quatara.backend.config;

import io.quatara.backend.security.ClerkAccessDeniedHandler;
import io.quatara.backend.security.ClerkAuthenticationEntryPoint;
import io.quatara.backend.security.ClerkJwtAuthenticationConverter;
import io.quatara.backend.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ClerkJwtAuthenticationConverter clerkJwtAuthenticationConverter;
    private final ClerkAuthenticationEntryPoint clerkAuthenticationEntryPoint;
    private final ClerkAccessDeniedHandler clerkAccessDeniedHandler;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req -> req
                        .anyRequest().authenticated())
                .oauth2ResourceServer(auth2 -> auth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(clerkJwtAuthenticationConverter))
                        .accessDeniedHandler(clerkAccessDeniedHandler)
                        .authenticationEntryPoint(clerkAuthenticationEntryPoint)
        );
        return http.build();
    }
}
