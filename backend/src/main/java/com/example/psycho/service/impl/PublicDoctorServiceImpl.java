package com.example.psycho.service.impl;

import com.example.psycho.dto.PublicDoctorDetailsDto;
import com.example.psycho.dto.PublicDoctorSummaryDto;
import com.example.psycho.model.UserEntity;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.PublicDoctorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class PublicDoctorServiceImpl implements PublicDoctorService {
    private static final Set<UserRole> DOCTOR_ROLES = EnumSet.of(UserRole.DOCTOR, UserRole.PSYCHOLOGIST);

    private final UserRepository userRepository;

    public PublicDoctorServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<PublicDoctorSummaryDto> getDoctors(String query, Pageable pageable) {
        String normalizedQuery = query == null ? "" : query.trim();
        return userRepository.searchActiveByRolesAndQuery(DOCTOR_ROLES, normalizedQuery, pageable)
                .map(this::toSummaryDto);
    }

    @Override
    public PublicDoctorDetailsDto getDoctorById(Long id) {
        UserEntity doctor = userRepository.findByIdAndRoleInAndActiveTrue(id, DOCTOR_ROLES)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found by id: " + id));
        return toDetailsDto(doctor);
    }

    private PublicDoctorSummaryDto toSummaryDto(UserEntity doctor) {
        return new PublicDoctorSummaryDto(
                doctor.getId(),
                doctor.getName(),
                nullIfBlank(doctor.getSpecialization()),
                doctor.getClinic() != null ? doctor.getClinic().getName() : null,
                toShortBio(doctor.getAbout()),
                null,
                doctor.getYearsOfExperience(),
                buildTags(doctor)
        );
    }

    private PublicDoctorDetailsDto toDetailsDto(UserEntity doctor) {
        return new PublicDoctorDetailsDto(
                doctor.getId(),
                doctor.getName(),
                nullIfBlank(doctor.getSpecialization()),
                doctor.getClinic() != null ? doctor.getClinic().getName() : null,
                doctor.getClinic() != null ? doctor.getClinic().getCity() : null,
                doctor.getClinic() != null ? doctor.getClinic().getAddress() : null,
                toShortBio(doctor.getAbout()),
                nullIfBlank(doctor.getAbout()),
                null,
                doctor.getYearsOfExperience(),
                buildTags(doctor)
        );
    }

    private List<String> buildTags(UserEntity doctor) {
        List<String> tags = new ArrayList<>();
        String specialization = nullIfBlank(doctor.getSpecialization());
        if (specialization != null) {
            for (String piece : specialization.split(",")) {
                String value = piece.trim();
                if (!value.isEmpty() && !tags.contains(value)) {
                    tags.add(value);
                }
            }
            if (tags.isEmpty()) {
                tags.add(specialization);
            }
        }
        String roleTag = doctor.getRole() == UserRole.PSYCHOLOGIST ? "Psychology" : "Medicine";
        if (!tags.contains(roleTag)) {
            tags.add(roleTag);
        }
        return tags;
    }

    private String toShortBio(String value) {
        String normalized = nullIfBlank(value);
        if (normalized == null) {
            return null;
        }
        if (normalized.length() <= 180) {
            return normalized;
        }
        return normalized.substring(0, 177) + "...";
    }

    private String nullIfBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
