package io.quatara.backend.dto.webhook.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClerkWebhookRequest {
    @JsonProperty("type")
    private String type; // e.g., "user.created"

    @JsonProperty("data")
    private ClerkUserData data;



}
