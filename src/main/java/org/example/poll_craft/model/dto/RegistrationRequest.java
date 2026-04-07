package org.example.poll_craft.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotBlank(message = "Username обязателен")
        @Size(min = 3, max = 50)
        String username,

        @NotBlank @Email
        String email,

        @NotBlank
        @Size(min = 6, message = "Пароль минимум 6 символов")
        String password

) {
}
