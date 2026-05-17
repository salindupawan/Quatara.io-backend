package io.quatara.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemoRequest {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email must not be blank")
    private String email;
}
