package io.quatara.backend.repository;

import io.quatara.backend.entity.ProjectShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectShareRepository extends JpaRepository<ProjectShare, UUID> {
    Optional<ProjectShare> findByToken(String token);
}
