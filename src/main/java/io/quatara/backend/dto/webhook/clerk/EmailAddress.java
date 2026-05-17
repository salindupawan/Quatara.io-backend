package io.quatara.backend.dto.webhook.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EmailAddress {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("verification")
    private EmailVerification verification;

}
