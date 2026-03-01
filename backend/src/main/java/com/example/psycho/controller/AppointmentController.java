package com.example.psycho.controller;

import com.example.psycho.dto.*;
import com.example.psycho.model.UserEntity;
import com.example.psycho.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<AppointmentResponseDto> create(@Valid @RequestBody AppointmentRequestDto request,
                                                         @AuthenticationPrincipal UserEntity actor) {
        return ResponseEntity.ok(appointmentService.createAppointment(request, actor.getEmail()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<AppointmentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable Long id,
                                                         @RequestBody AppointmentUpdateDto request,
                                                         @AuthenticationPrincipal UserEntity actor) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request, actor.getEmail()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<AppointmentResponseDto> changeStatus(@PathVariable Long id,
                                                               @RequestBody AppointmentStatusChangeDto request,
                                                               @AuthenticationPrincipal UserEntity actor) {
        return ResponseEntity.ok(appointmentService.changeStatus(id, request, actor.getEmail()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<Page<AppointmentResponseDto>> list(@RequestParam(required = false) Long patientId,
                                                             @RequestParam(required = false) Long doctorId,
                                                             Pageable pageable) {
        if (patientId != null && doctorId != null) {
            throw new IllegalArgumentException("Use either patientId or doctorId, not both");
        }
        if (patientId != null) {
            return ResponseEntity.ok(appointmentService.findByPatientId(patientId, pageable));
        }
        if (doctorId != null) {
            return ResponseEntity.ok(appointmentService.findByDoctorId(doctorId, pageable));
        }
        throw new IllegalArgumentException("patientId or doctorId query parameter is required");
    }

    @GetMapping("/calendar/doctors/{doctorId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<List<AppointmentResponseDto>> doctorCalendar(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Parameter 'to' must be greater or equal to 'from'");
        }
        return ResponseEntity.ok(appointmentService.getDoctorCalendar(doctorId, from, to));
    }

    @GetMapping("/clinics/{clinicId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponseDto>> clinicSchedule(@PathVariable Long clinicId) {
        return ResponseEntity.ok(appointmentService.findByClinicId(clinicId));
    }
}

