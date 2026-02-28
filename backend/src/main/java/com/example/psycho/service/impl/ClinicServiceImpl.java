package com.example.psycho.service.impl;

import com.example.psycho.dto.ClinicRequestDto;
import com.example.psycho.dto.ClinicResponseDto;
import com.example.psycho.dto.DepartmentCreateRequestDto;
import com.example.psycho.dto.DepartmentResponseDto;
import com.example.psycho.dto.MedicalServiceCreateRequestDto;
import com.example.psycho.dto.MedicalServiceResponseDto;
import com.example.psycho.exception.ResourceConflictException;
import com.example.psycho.model.ClinicEntity;
import com.example.psycho.model.DepartmentEntity;
import com.example.psycho.model.MedicalServiceEntity;
import com.example.psycho.mapper.ClinicMapper;
import com.example.psycho.repository.ClinicRepository;
import com.example.psycho.repository.DepartmentRepository;
import com.example.psycho.repository.MedicalServiceRepository;
import com.example.psycho.service.ClinicService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {
    private final ClinicRepository clinicRepository;
    private final DepartmentRepository departmentRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final ClinicMapper clinicMapper;

    public ClinicServiceImpl(ClinicRepository clinicRepository,
                             DepartmentRepository departmentRepository,
                             MedicalServiceRepository medicalServiceRepository,
                             ClinicMapper clinicMapper) {
        this.clinicRepository = clinicRepository;
        this.departmentRepository = departmentRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.clinicMapper = clinicMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicResponseDto> getClinics() {
        return clinicRepository.findAll().stream().map(clinicMapper::toClinicDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicResponseDto getClinicById(Long id) {
        ClinicEntity clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clinic not found by id: " + id));
        return clinicMapper.toClinicDto(clinic);
    }

    @Override
    public ClinicResponseDto createClinic(ClinicRequestDto request) {
        if (clinicRepository.existsByName(request.name())) {
            throw new ResourceConflictException("Clinic with this name already exists");
        }

        ClinicEntity clinic = new ClinicEntity();
        apply(clinic, request);

        return clinicMapper.toClinicDto(clinicRepository.save(clinic));
    }

    @Override
    public ClinicResponseDto updateClinic(Long id, ClinicRequestDto request) {
        ClinicEntity clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clinic not found by id: " + id));

        apply(clinic, request);
        return clinicMapper.toClinicDto(clinicRepository.save(clinic));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getDepartmentsByClinic(Long clinicId) {
        return departmentRepository.findByClinic_Id(clinicId)
                .stream()
                .map(clinicMapper::toDepartmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalServiceResponseDto> getServicesByDepartment(Long departmentId) {
        return medicalServiceRepository.findByDepartment_Id(departmentId)
                .stream()
                .map(clinicMapper::toServiceDto)
                .toList();
    }

    @Override
    public DepartmentResponseDto createDepartment(Long clinicId, DepartmentCreateRequestDto request) {
        ClinicEntity clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic not found by id: " + clinicId));

        if (departmentRepository.findByClinic_IdAndName(clinicId, request.name()).isPresent()) {
            throw new ResourceConflictException("Department with this name already exists in clinic");
        }

        DepartmentEntity department = new DepartmentEntity();
        department.setClinic(clinic);
        department.setName(request.name());
        department.setDescription(request.description());

        return clinicMapper.toDepartmentDto(departmentRepository.save(department));
    }

    @Override
    public MedicalServiceResponseDto createMedicalService(Long departmentId, MedicalServiceCreateRequestDto request) {
        DepartmentEntity department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found by id: " + departmentId));

        if (medicalServiceRepository.existsByCode(request.code())) {
            throw new ResourceConflictException("Medical service code already exists");
        }

        MedicalServiceEntity service = new MedicalServiceEntity();
        service.setDepartment(department);
        service.setCode(request.code());
        service.setName(request.name());
        service.setDescription(request.description());
        service.setDurationMinutes(request.durationMinutes());
        service.setBasePrice(request.basePrice());

        return clinicMapper.toServiceDto(medicalServiceRepository.save(service));
    }

    private void apply(ClinicEntity clinic, ClinicRequestDto request) {
        clinic.setName(request.name());
        clinic.setCity(request.city());
        clinic.setAddress(request.address());
        clinic.setPhone(request.phone());
        clinic.setEmail(request.email());
        clinic.setDescription(request.description());
        if (request.active() != null) {
            clinic.setActive(request.active());
        }
    }
}

