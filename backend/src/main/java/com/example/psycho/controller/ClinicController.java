package com.example.psycho.controller;

import com.example.psycho.dto.ClinicRequestDto;
import com.example.psycho.dto.ClinicResponseDto;
import com.example.psycho.dto.DepartmentCreateRequestDto;
import com.example.psycho.dto.DepartmentResponseDto;
import com.example.psycho.dto.MedicalServiceCreateRequestDto;
import com.example.psycho.dto.MedicalServiceResponseDto;
import com.example.psycho.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinics")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponseDto>> getClinics() {
        return ResponseEntity.ok(clinicService.getClinics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.getClinicById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<ClinicResponseDto> create(@Valid @RequestBody ClinicRequestDto request) {
        return ResponseEntity.ok(clinicService.createClinic(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<ClinicResponseDto> update(@PathVariable Long id,
                                                    @Valid @RequestBody ClinicRequestDto request) {
        return ResponseEntity.ok(clinicService.updateClinic(id, request));
    }

    @GetMapping("/{clinicId}/departments")
    public ResponseEntity<List<DepartmentResponseDto>> getDepartments(@PathVariable Long clinicId) {
        return ResponseEntity.ok(clinicService.getDepartmentsByClinic(clinicId));
    }

    @PostMapping("/{clinicId}/departments")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<DepartmentResponseDto> createDepartment(@PathVariable Long clinicId,
                                                                  @Valid @RequestBody DepartmentCreateRequestDto request) {
        return ResponseEntity.ok(clinicService.createDepartment(clinicId, request));
    }

    @GetMapping("/departments/{departmentId}/services")
    public ResponseEntity<List<MedicalServiceResponseDto>> getServices(@PathVariable Long departmentId) {
        return ResponseEntity.ok(clinicService.getServicesByDepartment(departmentId));
    }

    @PostMapping("/departments/{departmentId}/services")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<MedicalServiceResponseDto> createService(@PathVariable Long departmentId,
                                                                   @Valid @RequestBody MedicalServiceCreateRequestDto request) {
        return ResponseEntity.ok(clinicService.createMedicalService(departmentId, request));
    }
}

