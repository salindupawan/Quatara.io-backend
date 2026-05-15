package io.quatara.backend.config;

import io.quatara.backend.security.ClerkAccessDeniedHandler;
import io.quatara.backend.security.ClerkAuthenticationEntryPoint;
import io.quatara.backend.security.ClerkJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ClerkJwtAuthenticationConverter clerkJwtAuthenticationConverter;
    private final ClerkAuthenticationEntryPoint clerkAuthenticationEntryPoint;
    private final ClerkAccessDeniedHandler clerkAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req ->
                req.anyRequest().authenticated()
        ).oauth2ResourceServer(auth2 -> auth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(clerkJwtAuthenticationConverter))
                .accessDeniedHandler(clerkAccessDeniedHandler)
                .authenticationEntryPoint(clerkAuthenticationEntryPoint)
        );
        return http.build();
    }
}
