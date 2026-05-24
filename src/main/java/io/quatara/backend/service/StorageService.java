package io.quatara.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final S3Presigner s3Presigner;

    @Value("${tigris.bucket-name}")
    private String bucketName;

    public String generatePreSignedUploadUrl(String objectKey, String contentType) {
        // 1. Define the targets for the upload request
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType) // Enforcing content type ensures clients send the exact file type
                .build();

        // 2. Configure expiration time window (e.g., 15 minutes)
        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        // 3. Generate the signed URL execution sequence
        PresignedPutObjectRequest preSignedRequest = s3Presigner.presignPutObject(preSignRequest);

        return preSignedRequest.url().toString();
    }

    public String generatePreSignedDownloadUrl(String objectKey) {
        // 1. Specify the target bucket and exact file key path
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .responseContentType("application/pdf")
                .build();

        // 2. Set the expiration deadline (valid for 90 minutes)
        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(90))
                .getObjectRequest(getObjectRequest)
                .build();

        // 3. Cryptographically sign the request structural payload
        PresignedGetObjectRequest preSignedRequest = s3Presigner.presignGetObject(preSignRequest);
        return preSignedRequest.url().toString();
    }
}
