package io.quatara.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnboardingResponse {
    private String clientName;
    private String projectName;
    private BigDecimal depositAmount;
    private String token;

}
