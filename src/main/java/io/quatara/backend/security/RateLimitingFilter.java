package io.quatara.backend.security;

import io.github.bucket4j.Bucket;
import io.quatara.backend.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Resolve a unique identifier for the client (Token-based or IP-based)
        String clientKey = resolveClientKey(request);

        // 2. Fetch the client's matching rate-limiting bucket
        Bucket bucket = rateLimitingService.resolveBucket(clientKey);

        // 3. Try to consume 1 token from the bucket
        if (bucket.tryConsume(1)) {
            // Client is within limits, add helpful compliance tracking headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Client has exceeded limits, short-circuit immediately with a 429 Too Many Requests response
            sendTooManyRequestsResponse(response);
        }
    }

    private String resolveClientKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Isolate the unique token string as the key to prevent an attacker from bypassing limits by cycling IPs
            return authHeader.substring(7);
        }
        // Fallback to IP address tracking if the request is unauthenticated or exploring public paths
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendTooManyRequestsResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Retry-After", "60"); // Advise the client to back off for 60 seconds

        String jsonPayload = """
        {
            "timestamp": %d,
            "status": 429,
            "error": "Too Many Requests",
            "message": "You have exceeded your request limits. Please slow down and try again later."
        }
        """.formatted(System.currentTimeMillis());

        response.getWriter().write(jsonPayload);
    }
}
