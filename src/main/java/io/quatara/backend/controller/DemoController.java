package io.quatara.backend.controller;

import io.quatara.backend.dto.DemoRequest;
import io.quatara.backend.security.ClerkUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    // Endpoint to trigger MethodArgumentNotValidException via validation
    @PostMapping("/validate")
    public ResponseEntity<?> validateDemo(@Valid @RequestBody DemoRequest request) {
        // If validation passes, just echo the received data
        return ResponseEntity.ok(Map.of(
                "message", "Validation succeeded",
                "name", request.getName(),
                "email", request.getEmail()
        ));
    }

    // Demo endpoint to trigger a runtime exception and show global handling
    @GetMapping("/demo-error")
    public ResponseEntity<?> demoError() {
        throw new io.quatara.backend.exception.BadRequestException("Demo Bad Request Exception triggered");
    }
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal ClerkUserPrincipal user) {
        Map<String, Object> profileData = Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "authorities", user.getAuthorities()
        );
        return ResponseEntity.ok(profileData);
    }

    @GetMapping("m")
    public ResponseEntity<?> m() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminData(@AuthenticationPrincipal ClerkUserPrincipal user) {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to the admin area, " + user.getFirstName(),
                "status", "Success"
        ));
    }
}
