package com.example.catalog.module.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request payload")
public class LoginRequest {
    @NotBlank
    @Email
    @Schema(description = "Registered email address", example = "vinicius@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Account password", example = "SecurePass1!")
    private String password;
}
