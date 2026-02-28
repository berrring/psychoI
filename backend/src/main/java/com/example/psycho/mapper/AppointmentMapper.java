package com.example.psycho.mapper;

import com.example.psycho.dto.AppointmentResponseDto;
import com.example.psycho.model.AppointmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentResponseDto toDto(AppointmentEntity appointment) {
        Long clinicId = appointment.getClinic() != null ? appointment.getClinic().getId() : null;
        String clinicName = appointment.getClinic() != null ? appointment.getClinic().getName() : null;

        Long departmentId = appointment.getDepartment() != null ? appointment.getDepartment().getId() : null;
        String departmentName = appointment.getDepartment() != null ? appointment.getDepartment().getName() : null;

        Long medicalServiceId = appointment.getMedicalService() != null ? appointment.getMedicalService().getId() : null;
        String medicalServiceName = appointment.getMedicalService() != null ? appointment.getMedicalService().getName() : null;

        return new AppointmentResponseDto(
                appointment.getId(),
                appointment.getClient().getId(),
                appointment.getClient().getName(),
                appointment.getPsychologist().getId(),
                appointment.getPsychologist().getName(),
                clinicId,
                clinicName,
                departmentId,
                departmentName,
                medicalServiceId,
                medicalServiceName,
                appointment.getTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getComplaint(),
                appointment.getDiagnosis(),
                appointment.getTreatmentPlan(),
                appointment.getNotes(),
                appointment.getCancellationReason()
        );
    }
}

