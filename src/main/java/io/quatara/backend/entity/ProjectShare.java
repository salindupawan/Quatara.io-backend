package io.quatara.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_shares", indexes = @Index(name = "idx_token", columnList = "token"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectShare extends CreatedAtBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // The cryptographically secure random string
    @Column(name = "token", nullable = false, unique = true, length = 32)
    private String token;

    // Relationship back to main Project entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Helper method to check if the link is dead
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

}
