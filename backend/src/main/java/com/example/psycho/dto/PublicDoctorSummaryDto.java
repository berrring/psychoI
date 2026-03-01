package com.example.psycho.dto;

import java.util.List;

public record PublicDoctorSummaryDto(
        Long id,
        String fullName,
        String specialization,
        String clinic,
        String shortBio,
        String avatarUrl,
        Integer experienceYears,
        List<String> tags
) {
}
