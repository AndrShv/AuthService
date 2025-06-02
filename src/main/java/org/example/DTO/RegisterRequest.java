package org.example.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Email(message = "Invalid email format")
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Phone must start with +380 and contain 9 digits")
        String phone,

        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank
        String confirmPassword
) {}

