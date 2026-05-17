package io.quatara.backend.controller;

import io.quatara.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @GetMapping("/pre-signed/upload")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {

        // Generate a clean, unique object key name to prevent target collision overrides
        String extension = filename.substring(filename.lastIndexOf("."));
        String uniqueObjectKey = "uploads/" + UUID.randomUUID() + extension;

        String uploadUrl = storageService.generatePreSignedUploadUrl(uniqueObjectKey, contentType);

        // Return both the URL and the target unique key back to the UI
        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl,
                "fileKey", uniqueObjectKey
        ));
    }
}
