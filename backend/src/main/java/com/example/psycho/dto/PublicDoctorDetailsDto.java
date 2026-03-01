package com.example.psycho.dto;

import java.util.List;

public record PublicDoctorDetailsDto(
        Long id,
        String fullName,
        String specialization,
        String clinic,
        String clinicCity,
        String clinicAddress,
        String shortBio,
        String fullBio,
        String avatarUrl,
        Integer experienceYears,
        List<String> tags
) {
}
