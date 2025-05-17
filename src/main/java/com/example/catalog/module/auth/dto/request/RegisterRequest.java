package com.example.catalog.module.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request payload")
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Schema(description = "Valid email address", example = "vinicius@example.com")
    private String email;

    @Size(min = 8, max = 36)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*_.]).{8,36}$",
            message = "Password must be between 8 and 36 characters long and include at least one lowercase letter, one uppercase letter, one number, and one special character (e.g., !@#$%&*_.)."
    )
    @Schema(
            description = "Password with at least 1 lowercase, 1 uppercase, 1 number, and 1 special character",
            example = "SecurePass1!"
    )
    private String password;
}
