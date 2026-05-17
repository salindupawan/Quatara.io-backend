package io.quatara.backend.config;

import io.quatara.backend.security.ClerkAccessDeniedHandler;
import io.quatara.backend.security.ClerkAuthenticationEntryPoint;
import io.quatara.backend.security.ClerkJwtAuthenticationConverter;
import io.quatara.backend.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ClerkJwtAuthenticationConverter clerkJwtAuthenticationConverter;
    private final ClerkAuthenticationEntryPoint clerkAuthenticationEntryPoint;
    private final ClerkAccessDeniedHandler clerkAccessDeniedHandler;
    private final RateLimitingFilter rateLimitingFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, "/api/v1/webhook/clerk/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/demo-error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/validate").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(auth2 -> auth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(clerkJwtAuthenticationConverter))
                        .accessDeniedHandler(clerkAccessDeniedHandler)
                        .authenticationEntryPoint(clerkAuthenticationEntryPoint)
        );
        return http.build();
    }
}
