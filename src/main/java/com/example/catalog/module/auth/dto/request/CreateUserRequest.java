package com.example.catalog.module.auth.dto.request;

import com.example.catalog.module.auth.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new user by an authenticated user")
public class CreateUserRequest {

    @NotBlank
    @Schema(description = "User full name", example = "Kelly Speria")
    private String name;

    @NotBlank
    @Email
    @Schema(description = "Unique email address", example = "kelly@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 36)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*_.]).{8,36}$",
            message = "Password must be between 8 and 36 characters long and include at least one lowercase letter, one uppercase letter, one number, and one special character (e.g., !@#$%&*_.)."
    )
    @Schema(description = "Password with strong requirements", example = "StrongPass123!")
    private String password;

    @NotNull
    @Schema(description = "User role to be assigned", example = "VENDOR")
    private Role role;
}
