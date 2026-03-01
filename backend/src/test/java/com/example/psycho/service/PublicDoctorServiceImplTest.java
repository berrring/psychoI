package com.example.psycho.service;

import com.example.psycho.dto.PublicDoctorDetailsDto;
import com.example.psycho.dto.PublicDoctorSummaryDto;
import com.example.psycho.model.ClinicEntity;
import com.example.psycho.model.UserEntity;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.impl.PublicDoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicDoctorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private PublicDoctorServiceImpl publicDoctorService;

    @BeforeEach
    void setUp() {
        publicDoctorService = new PublicDoctorServiceImpl(userRepository);
    }

    @Test
    void getDoctorsShouldReturnPublicSafeSummary() {
        UserEntity doctor = buildDoctor();

        when(userRepository.searchActiveByRolesAndQuery(anyCollection(), eq(""), any()))
                .thenReturn(new PageImpl<>(List.of(doctor)));

        List<PublicDoctorSummaryDto> items = publicDoctorService.getDoctors(null, PageRequest.of(0, 10)).getContent();

        assertEquals(1, items.size());
        assertEquals("Dr. Alex Morgan", items.get(0).fullName());
        assertEquals("Internal medicine", items.get(0).specialization());
        assertEquals("Bering Central Clinic", items.get(0).clinic());
        assertNull(items.get(0).avatarUrl());
        assertFalse(items.get(0).tags().isEmpty());
    }

    @Test
    void getDoctorByIdShouldReturnPublicDetailsWithoutSensitiveFields() {
        UserEntity doctor = buildDoctor();
        when(userRepository.findByIdAndRoleInAndActiveTrue(eq(doctor.getId()), anyCollection()))
                .thenReturn(Optional.of(doctor));

        PublicDoctorDetailsDto details = publicDoctorService.getDoctorById(doctor.getId());

        assertEquals("Dr. Alex Morgan", details.fullName());
        assertEquals("Astana", details.clinicCity());
        assertEquals("Arsenal Avenue 101", details.clinicAddress());
        assertEquals("Internal medicine", details.specialization());
        assertNull(details.avatarUrl());
    }

    private UserEntity buildDoctor() {
        ClinicEntity clinic = new ClinicEntity();
        clinic.setId(10L);
        clinic.setName("Bering Central Clinic");
        clinic.setCity("Astana");
        clinic.setAddress("Arsenal Avenue 101");

        UserEntity doctor = new UserEntity();
        doctor.setId(100L);
        doctor.setName("Dr. Alex Morgan");
        doctor.setEmail("doc.alex@clinic.local");
        doctor.setRole(UserRole.DOCTOR);
        doctor.setSpecialization("Internal medicine");
        doctor.setClinic(clinic);
        doctor.setAbout("Specialist in internal medicine and preventive chronic disease management.");
        doctor.setYearsOfExperience(12);
        doctor.setActive(true);
        return doctor;
    }
}
