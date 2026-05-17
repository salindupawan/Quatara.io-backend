package io.quatara.backend.repository;

import io.quatara.backend.entity.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
    // add custom queries if needed
}
