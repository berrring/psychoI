package com.example.psycho.mapper;

import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        Long clinicId = user.getClinic() != null ? user.getClinic().getId() : null;
        String clinicName = user.getClinic() != null ? user.getClinic().getName() : null;

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getPhone(),
                user.getSpecialization(),
                user.getYearsOfExperience(),
                user.getAbout(),
                clinicId,
                clinicName,
                user.isActive()
        );
    }
}

