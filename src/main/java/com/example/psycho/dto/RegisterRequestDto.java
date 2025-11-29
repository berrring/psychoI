package com.example.psycho.dto;

import com.example.psycho.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull String password,
        @NotNull UserRole role
        ){
}
