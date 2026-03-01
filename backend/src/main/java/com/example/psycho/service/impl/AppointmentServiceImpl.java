package com.example.psycho.service.impl;

import com.example.psycho.dto.*;
import com.example.psycho.exception.ResourceConflictException;
import com.example.psycho.model.*;
import com.example.psycho.mapper.AppointmentMapper;
import com.example.psycho.model.AppointmentStatus;
import com.example.psycho.model.AuditAction;
import com.example.psycho.model.MessageType;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.*;
import com.example.psycho.service.AppointmentService;
import com.example.psycho.service.AuditService;
import com.example.psycho.service.IdentityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private static final Set<UserRole> DOCTOR_ROLES = EnumSet.of(UserRole.DOCTOR, UserRole.PSYCHOLOGIST);
    private static final Set<UserRole> PATIENT_ROLES = EnumSet.of(UserRole.PATIENT, UserRole.CLIENT);

    private static final Set<AppointmentStatus> ACTIVE_STATUSES = EnumSet.of(
            AppointmentStatus.BOOKED,
            AppointmentStatus.CREATED,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.IN_PROGRESS
    );

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DepartmentRepository departmentRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final MessageRepository messageRepository;
    private final AppointmentMapper appointmentMapper;
    private final AuditService auditService;
    private final IdentityService identityService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  ClinicRepository clinicRepository,
                                  DepartmentRepository departmentRepository,
                                  MedicalServiceRepository medicalServiceRepository,
                                  MessageRepository messageRepository,
                                  AppointmentMapper appointmentMapper,
                                  AuditService auditService,
                                  IdentityService identityService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.departmentRepository = departmentRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.messageRepository = messageRepository;
        this.appointmentMapper = appointmentMapper;
        this.auditService = auditService;
        this.identityService = identityService;
    }

    @Override
    public AppointmentResponseDto createAppointment(AppointmentRequestDto request, String actorEmail) {
        UserEntity actor = identityService.requireByEmail(actorEmail);
        UserEntity patient = requireUserById(request.patientId());
        UserEntity doctor = requireUserById(request.doctorId());

        requireRole(patient, PATIENT_ROLES, "patient");
        requireRole(doctor, DOCTOR_ROLES, "doctor");

        ClinicEntity clinic = clinicRepository.findById(request.clinicId())
                .orElseThrow(() -> new EntityNotFoundException("Clinic not found by id: " + request.clinicId()));

        DepartmentEntity department = null;
        if (request.departmentId() != null) {
            department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found by id: " + request.departmentId()));
            if (!department.getClinic().getId().equals(clinic.getId())) {
                throw new IllegalArgumentException("Department does not belong to selected clinic");
            }
        }

        MedicalServiceEntity medicalService = null;
        if (request.medicalServiceId() != null) {
            medicalService = medicalServiceRepository.findById(request.medicalServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Medical service not found by id: " + request.medicalServiceId()));
            if (department != null && !medicalService.getDepartment().getId().equals(department.getId())) {
                throw new IllegalArgumentException("Medical service does not belong to selected department");
            }
        }

        if (appointmentRepository.existsByPsychologist_IdAndTimeAndStatusIn(doctor.getId(), request.time(), ACTIVE_STATUSES)) {
            throw new ResourceConflictException("Doctor is busy at selected time");
        }

        int duration = request.durationMinutes() != null ? request.durationMinutes()
                : medicalService != null ? medicalService.getDurationMinutes() : 30;

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setClient(patient);
        appointment.setPsychologist(doctor);
        appointment.setClinic(clinic);
        appointment.setDepartment(department);
        appointment.setMedicalService(medicalService);
        appointment.setTime(request.time());
        appointment.setEndTime(request.time().plusMinutes(duration));
        appointment.setStatus(AppointmentStatus.CREATED);
        appointment.setComplaint(request.complaint());
        appointment.setNotes(request.notes());
        appointment.setCreatedBy(actor);

        AppointmentEntity saved = appointmentRepository.save(appointment);

        saveSystemEvent(saved, actor, "Visit created");
        auditService.log("appointments", saved.getId(), AuditAction.CREATE, actor,
                "Created visit for patientId=" + patient.getId() + " doctorId=" + doctor.getId());

        return appointmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getById(Long id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found by id: " + id));
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto request, String actorEmail) {
        UserEntity actor = identityService.requireByEmail(actorEmail);
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found by id: " + id));

        if (request.doctorId() != null) {
            UserEntity doctor = requireUserById(request.doctorId());
            requireRole(doctor, DOCTOR_ROLES, "doctor");
            appointment.setPsychologist(doctor);
        }

        if (request.departmentId() != null) {
            DepartmentEntity department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found by id: " + request.departmentId()));
            appointment.setDepartment(department);
        }

        if (request.medicalServiceId() != null) {
            MedicalServiceEntity medicalService = medicalServiceRepository.findById(request.medicalServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Medical service not found by id: " + request.medicalServiceId()));
            appointment.setMedicalService(medicalService);
        }

        if (request.time() != null) {
            boolean busyAtTime = appointmentRepository.findByPsychologist_IdAndTimeAndStatusIn(
                            appointment.getPsychologist().getId(), request.time(), ACTIVE_STATUSES)
                    .stream()
                    .anyMatch(existing -> !existing.getId().equals(appointment.getId()));
            if (busyAtTime) {
                throw new ResourceConflictException("Doctor is busy at selected time");
            }
            appointment.setTime(request.time());

            int duration = request.durationMinutes() != null ? request.durationMinutes()
                    : appointment.getMedicalService() != null ? appointment.getMedicalService().getDurationMinutes() : 30;
            appointment.setEndTime(request.time().plusMinutes(duration));
        }

        if (request.complaint() != null) {
            appointment.setComplaint(request.complaint());
        }
        if (request.diagnosis() != null) {
            appointment.setDiagnosis(request.diagnosis());
        }
        if (request.treatmentPlan() != null) {
            appointment.setTreatmentPlan(request.treatmentPlan());
        }
        if (request.notes() != null) {
            appointment.setNotes(request.notes());
        }

        AppointmentEntity saved = appointmentRepository.save(appointment);

        saveSystemEvent(saved, actor, "Visit data updated");
        auditService.log("appointments", saved.getId(), AuditAction.UPDATE, actor,
                "Updated visit fields");

        return appointmentMapper.toDto(saved);
    }

    @Override
    public AppointmentResponseDto changeStatus(Long id, AppointmentStatusChangeDto request, String actorEmail) {
        UserEntity actor = identityService.requireByEmail(actorEmail);
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found by id: " + id));

        appointment.setStatus(request.status());

        if (request.status() == AppointmentStatus.CANCELLED && request.reason() != null) {
            appointment.setCancellationReason(request.reason());
        }

        if (request.status() == AppointmentStatus.COMPLETED && appointment.getEndTime() == null) {
            appointment.setEndTime(LocalDateTime.now());
        }

        AppointmentEntity saved = appointmentRepository.save(appointment);

        String reasonSuffix = request.reason() != null ? ": " + request.reason() : "";
        saveSystemEvent(saved, actor, "Status changed to " + request.status() + reasonSuffix);
        auditService.log("appointments", saved.getId(), AuditAction.STATUS_CHANGE, actor,
                "Status changed to " + request.status());

        return appointmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findByPatientId(Long patientId, Pageable pageable) {
        return appointmentRepository.findByClient_Id(patientId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findByDoctorId(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByPsychologist_Id(doctorId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<AppointmentResponseDto> findByClinicId(Long clinicId) {
        return appointmentRepository.findByClinic_IdOrderByTimeAsc(clinicId)
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<AppointmentResponseDto> getDoctorCalendar(Long doctorId, LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay().minusNanos(1);
        return appointmentRepository.findByPsychologist_IdAndTimeBetween(doctorId, fromDt, toDt)
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    private UserEntity requireUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
    }

    private void requireRole(UserEntity user, Set<UserRole> roles, String label) {
        if (!roles.contains(user.getRole())) {
            throw new IllegalArgumentException("Selected " + label + " has invalid role: " + user.getRole());
        }
    }

    private void saveSystemEvent(AppointmentEntity appointment, UserEntity actor, String text) {
        MessageEntity event = new MessageEntity();
        event.setAppointment(appointment);
        event.setSender(actor);
        event.setType(MessageType.SYSTEM);
        event.setText(text);
        messageRepository.save(event);
    }
}

