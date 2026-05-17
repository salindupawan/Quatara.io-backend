package io.quatara.backend.service;


import io.quatara.backend.dto.webhook.clerk.ClerkUserData;
import io.quatara.backend.entity.Organization;
import io.quatara.backend.entity.User;
import io.quatara.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void createFromClerk(ClerkUserData data) {
        // Avoid duplicate based on clerkId or email
        User existing = userRepository.findByClerkId(data.getId())
                .or(() -> userRepository.findByEmail(data.getEmail()))
                .orElse(null);
        if (existing != null) {
            log.info("User already exists: clerkId={}, email={}", existing.getClerkId(), existing.getEmail());
            return;
        }
        // Create new user
        User user = new User();
        user.setClerkId(data.getId());
        // Email: use computed primary email; fallback to placeholder if still null/empty
        String email = data.getEmail();
        if (email == null || email.isEmpty()) {
            email = data.getId() + "@placeholder.local";
        }
        user.setEmail(email);
        log.info("Creating new user: clerkId={}, email={}", data.getId(), email);
        // First and last name: use empty string if missing to satisfy not-null constraints later (if any)
        user.setFirstName(data.getFirstName() != null ? data.getFirstName() : "");
        user.setLastName(data.getLastName() != null ? data.getLastName() : "");
        // organization handling omitted for brevity
        Organization organization = Organization.builder()
                .name(user.getFirstName() + " " + user.getLastName())
                .clerkOrgId(data.getId())
                .build();
        user.setOrganization(organization);
        userRepository.save(user);
    }
}
