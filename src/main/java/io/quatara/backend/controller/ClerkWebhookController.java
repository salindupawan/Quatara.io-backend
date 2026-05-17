package io.quatara.backend.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import io.quatara.backend.dto.webhook.clerk.ClerkWebhookRequest;
import io.quatara.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhook/clerk")
@RequiredArgsConstructor
@Slf4j
public class ClerkWebhookController {
    private final UserService userService;

    @Value("${clerk.webhook.secret}")
    private String secret;

    @PostMapping("/user")
    public ResponseEntity<String> userCreated(@RequestBody String rawBody,
                                            @RequestHeader("svix-id") String svixId,
                                            @RequestHeader("svix-timestamp") String svixTimestamp,
                                            @RequestHeader("svix-signature") String svixSignature){
        // 1. Prepare Svix required headers bundle
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("svix-id", List.of(svixId));
        headerMap.put("svix-timestamp", List.of(svixTimestamp));
        headerMap.put("svix-signature", List.of(svixSignature));

        HttpHeaders headers = HttpHeaders.of(headerMap, (k, v) -> true);

        try {
            // 2. Cryptographically verify that the payload hasn't been tampered with and comes from Clerk
            Webhook webhook = new Webhook(secret);
            webhook.verify(rawBody, headers);

            // 3. Parse the verified payload safely
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClerkWebhookRequest request = objectMapper.readValue(rawBody, ClerkWebhookRequest.class);

            log.info("Received verified Clerk Webhook event type: {}", request.getType());

            // 4. Act on user.created event
            if ("user.created".equals(request.getType())) {
                log.info("Processing user.created for Clerk ID: {} ({})", request.getData().getId(), request.getData().getEmail());
                userService.createFromClerk(request.getData());
            }

            // Always return a clean 200/201 response quickly so Clerk knows it succeeded and stops retrying
            return ResponseEntity.ok("Webhook processed successfully");

        } catch (WebhookVerificationException e) {
            log.warn("SECURITY WARNING: Failed Clerk Webhook cryptographic signature verification attempt.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature confirmation");
        } catch (Exception e) {
            log.error("Internal failure processing verified webhook payload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing error");
        }
    }
}
