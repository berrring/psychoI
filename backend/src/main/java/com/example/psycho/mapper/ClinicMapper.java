package com.example.psycho.mapper;

import com.example.psycho.dto.ClinicResponseDto;
import com.example.psycho.dto.DepartmentResponseDto;
import com.example.psycho.dto.MedicalServiceResponseDto;
import com.example.psycho.model.ClinicEntity;
import com.example.psycho.model.DepartmentEntity;
import com.example.psycho.model.MedicalServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ClinicMapper {
    public ClinicResponseDto toClinicDto(ClinicEntity clinic) {
        return new ClinicResponseDto(
                clinic.getId(),
                clinic.getName(),
                clinic.getCity(),
                clinic.getAddress(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getDescription(),
                clinic.isActive()
        );
    }

    public DepartmentResponseDto toDepartmentDto(DepartmentEntity department) {
        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getClinic().getId(),
                department.getClinic().getName()
        );
    }

    public MedicalServiceResponseDto toServiceDto(MedicalServiceEntity service) {
        return new MedicalServiceResponseDto(
                service.getId(),
                service.getCode(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getBasePrice(),
                service.getDepartment().getId(),
                service.getDepartment().getName()
        );
    }
}

