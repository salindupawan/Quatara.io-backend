package io.quatara.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationRequest {
    @NotNull(message = "Annotation type is required")
    @Pattern(regexp = "^(SIGNATURE|DATE)$", message = "Type must be either SIGNATURE or DATE")
    private String type;

    @NotNull(message = "Page index is required")
    @Min(value = 0, message = "Page index cannot be negative")
    private Integer pageIndex;

    @NotNull(message = "xCoordinates are required")
    @Min(value = 0, message = "xCoordinates cannot be negative")
    private Double xCoordinates;

    @NotNull(message = "yCoordinates are required")
    @Min(value = 0, message = "yCoordinates cannot be negative")
    private Double yCoordinates;
}
