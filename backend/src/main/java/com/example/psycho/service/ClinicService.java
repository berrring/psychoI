package com.example.psycho.service;

import com.example.psycho.dto.ClinicRequestDto;
import com.example.psycho.dto.ClinicResponseDto;
import com.example.psycho.dto.DepartmentCreateRequestDto;
import com.example.psycho.dto.DepartmentResponseDto;
import com.example.psycho.dto.MedicalServiceCreateRequestDto;
import com.example.psycho.dto.MedicalServiceResponseDto;

import java.util.List;

public interface ClinicService {
    List<ClinicResponseDto> getClinics();

    ClinicResponseDto getClinicById(Long id);

    ClinicResponseDto createClinic(ClinicRequestDto request);

    ClinicResponseDto updateClinic(Long id, ClinicRequestDto request);

    List<DepartmentResponseDto> getDepartmentsByClinic(Long clinicId);

    List<MedicalServiceResponseDto> getServicesByDepartment(Long departmentId);

    DepartmentResponseDto createDepartment(Long clinicId, DepartmentCreateRequestDto request);

    MedicalServiceResponseDto createMedicalService(Long departmentId, MedicalServiceCreateRequestDto request);
}