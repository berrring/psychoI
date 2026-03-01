package com.example.psycho.service.impl;

import com.example.psycho.dto.ChangePasswordRequestDto;
import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.dto.UserUpdateDto;
import com.example.psycho.model.ClinicEntity;
import com.example.psycho.model.UserEntity;
import com.example.psycho.mapper.UserMapper;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.ClinicRepository;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Set<UserRole> DOCTOR_ROLES = EnumSet.of(UserRole.DOCTOR, UserRole.PSYCHOLOGIST);
    private static final Set<UserRole> PATIENT_ROLES = EnumSet.of(UserRole.PATIENT, UserRole.CLIENT);

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           ClinicRepository clinicRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getDoctors(String query, Pageable pageable) {
        String normalizedQuery = query == null ? "" : query.trim();
        return userRepository.searchActiveByRolesAndQuery(DOCTOR_ROLES, normalizedQuery, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getPatients(Pageable pageable) {
        return userRepository.findByRoleIn(PATIENT_ROLES, pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));

        if (updateDto.name() != null && !updateDto.name().isBlank()) {
            user.setName(updateDto.name());
        }
        if (updateDto.phone() != null) {
            user.setPhone(updateDto.phone());
        }
        if (updateDto.specialization() != null) {
            user.setSpecialization(updateDto.specialization());
        }
        if (updateDto.licenseNumber() != null) {
            user.setLicenseNumber(updateDto.licenseNumber());
        }
        if (updateDto.yearsOfExperience() != null) {
            user.setYearsOfExperience(updateDto.yearsOfExperience());
        }
        if (updateDto.about() != null) {
            user.setAbout(updateDto.about());
        }
        if (updateDto.active() != null) {
            user.setActive(updateDto.active());
        }
        if (updateDto.clinicId() != null) {
            ClinicEntity clinic = clinicRepository.findById(updateDto.clinicId())
                    .orElseThrow(() -> new EntityNotFoundException("Clinic not found by id: " + updateDto.clinicId()));
            user.setClinic(clinic);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is invalid");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}

