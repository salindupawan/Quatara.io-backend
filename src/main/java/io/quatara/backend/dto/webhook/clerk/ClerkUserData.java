package io.quatara.backend.dto.webhook.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClerkUserData {
    @JsonProperty("id")
    private String id; // Clerk user ID

    @JsonProperty("email_addresses")
    private List<EmailAddress> emailAddresses;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("organization_id")
    private String organizationId;

    // Compute primary email from emailAddresses list
    public String getEmail() {
        if (emailAddresses == null || emailAddresses.isEmpty()) return null;
        for (EmailAddress ea : emailAddresses) {
            if (ea.getVerification() != null && "verified".equalsIgnoreCase(ea.getVerification().getStatus())) {
                return ea.getEmailAddress();
            }
        }
        return emailAddresses.getFirst().getEmailAddress();
    }
}
