package com.example.psycho.service;

import com.example.psycho.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponseDto createAppointment(AppointmentRequestDto request, String actorEmail);

    AppointmentResponseDto getById(Long id);

    AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto request, String actorEmail);

    AppointmentResponseDto changeStatus(Long id, AppointmentStatusChangeDto request, String actorEmail);

    Page<AppointmentResponseDto> findByPatientId(Long patientId, Pageable pageable);

    Page<AppointmentResponseDto> findByDoctorId(Long doctorId, Pageable pageable);

    List<AppointmentResponseDto> findByClinicId(Long clinicId);

    List<AppointmentResponseDto> getDoctorCalendar(Long doctorId, LocalDate from, LocalDate to);
}
