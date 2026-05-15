package io.quatara.backend.controller;

import io.quatara.backend.security.ClerkUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminData(@AuthenticationPrincipal ClerkUserPrincipal user) {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to the admin area, " + user.getFirstName(),
                "status", "Success"
        ));
    }
}
