package io.quatara.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingDataRequest {
    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid email format")
    private String clientEmail;

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotBlank(message = "File key is required")
    private String fileKey;

    @NotNull(message = "Deposit amount is required")
    @Min(value = 0, message = "Deposit amount must be zero or a positive value")
    private BigDecimal depositAmount;

    @NotNull(message = "kycRequired flag is required")
    private Boolean kycRequired;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotNull(message = "Annotations list cannot be null")
    @Valid // Critical: Triggers cascade validation into the elements inside the array list
    private List<AnnotationRequest> annotations;
}
