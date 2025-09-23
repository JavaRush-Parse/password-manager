package ua.com.javarush.parse.m5.passwordmanager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        @NotBlank
        @Size(min = 8, max = 20)
        String email,
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
