package io.quatara.backend.controller;

import io.quatara.backend.dto.request.OnboardingDataRequest;
import io.quatara.backend.dto.response.OnboardingResponse;
import io.quatara.backend.entity.Project;
import io.quatara.backend.security.ClerkUserPrincipal;
import io.quatara.backend.service.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping
    public ResponseEntity<OnboardingResponse> onboardClient(@Valid @RequestBody OnboardingDataRequest request,
                                            @AuthenticationPrincipal ClerkUserPrincipal principal) {
        OnboardingResponse onboard = onboardingService.onboard(request, principal);
        return ResponseEntity.ok().body(onboard);
    }
}
