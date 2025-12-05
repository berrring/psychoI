package com.example.psycho.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {}