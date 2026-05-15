package io.quatara.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class SecurityResponseUtility {
    public static void writeJsonError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> map = Map.of(
                "timestamp",System.currentTimeMillis(),
                "status",status,
                "error", error,
                "message", message
        );

        String errorData = new ObjectMapper().writeValueAsString(map);
        response.getWriter().write(errorData);
    }
}
