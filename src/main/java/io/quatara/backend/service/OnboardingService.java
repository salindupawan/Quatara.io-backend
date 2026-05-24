package io.quatara.backend.service;

import io.quatara.backend.dto.request.AnnotationRequest;
import io.quatara.backend.dto.request.OnboardingDataRequest;
import io.quatara.backend.dto.response.OnboardingResponse;
import io.quatara.backend.entity.*;
import io.quatara.backend.exception.BadRequestException;
import io.quatara.backend.repository.*;
import io.quatara.backend.security.ClerkUserPrincipal;
import io.quatara.backend.util.TokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final AnnotationRepository annotationRepository;
    private final ProjectShareRepository projectShareRepository;

    @Transactional
    public OnboardingResponse onboard(OnboardingDataRequest request, ClerkUserPrincipal principal) {
        // Find the freelancer (user) based on the authenticated principal
        Optional<User> maybeUser = userRepository.findByClerkId(principal.getId());
        if (maybeUser.isEmpty()) {
            log.warn("Freelancer not found for clerkId {}", principal.getId());
            throw new BadRequestException("Freelancer not found for clerkId: "+ principal.getId());
        }
        Project project = getProject(request, maybeUser);
        project = projectRepository.save(project);

        // Create Document entity linked to the project
        Document document = new Document();
        document.setProject(project);
        document.setPdfUrl(request.getFileKey()); // store the provided file key / URL
        document = documentRepository.save(document);

        ProjectShare projectShare = ProjectShare.builder()
                .token(TokenGenerator.generateNewToken())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .project(project)
                .build();
        projectShareRepository.save(projectShare);

        // Persist annotations if any
        List<AnnotationRequest> annotationRequests = request.getAnnotations();
        if (annotationRequests != null && !annotationRequests.isEmpty()) {
            for (AnnotationRequest ar : annotationRequests) {
                Annotation annotation = new Annotation();
                annotation.setDocument(document);
                annotation.setPageIndex(ar.getPageIndex());
                annotation.setXCoordinate(ar.getXCoordinates());
                annotation.setYCoordinate(ar.getYCoordinates());
                annotation.setAnnotationType(ar.getType());
                annotationRepository.save(annotation);
            }
        }
        log.info("Onboarding completed for client {} (projectId={})", request.getClientEmail(), project.getId());
        return OnboardingResponse.builder()
                .clientName(request.getClientName())
                .projectName(request.getProjectName())
                .depositAmount(request.getDepositAmount())
                .token(projectShare.getToken())
                .build();
    }

    private Project getProject(OnboardingDataRequest request, Optional<User> maybeUser) {
        User freelancer = maybeUser.get();

        // Create Project entity
        Project project = new Project();
        project.setClientName(request.getClientName());
        project.setClientEmail(request.getClientEmail());
        project.setProjectName(request.getProjectName());
        // Convert deposit amount (BigDecimal) to cents to avoid floating point errors
        BigDecimal deposit = request.getDepositAmount() != null ? request.getDepositAmount() : BigDecimal.ZERO;
        project.setDepositAmount(deposit);
        // Set organization from freelancer's organization if present
        Organization org = freelancer.getOrganization();
        if (org != null) {
            project.setOrganization(org);
        }else {
            throw new BadRequestException("Organization not found");
        }
        return project;
    }
}
