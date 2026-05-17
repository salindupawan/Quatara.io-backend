package io.quatara.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_links")
public class PaymentLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "short_code", nullable = false, unique = true, length = 50)
    private String shortCode;

    @Column(name = "stripe_link_url", nullable = false, columnDefinition = "text")
    private String stripeLinkUrl;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "last_viewed_at")
    private Instant lastViewedAt;
}
