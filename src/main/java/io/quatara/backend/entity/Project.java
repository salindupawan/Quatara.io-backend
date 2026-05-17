package io.quatara.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "client_name", nullable = false, length = 255)
    private String clientName;

    @Column(name = "client_email", nullable = false, length = 255)
    private String clientEmail;

    @Column(name = "project_name", nullable = false, length = 255)
    private String projectName;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> documents;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentLink> paymentLinks;
}
