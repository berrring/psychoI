package com.example.psycho.service.impl;

import com.example.psycho.dto.DashboardSummaryDto;
import com.example.psycho.service.DashboardService;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.AppointmentRepository;
import com.example.psycho.repository.ClinicRepository;
import com.example.psycho.repository.KnowledgeArticleRepository;
import com.example.psycho.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final KnowledgeArticleRepository knowledgeArticleRepository;

    public DashboardServiceImpl(ClinicRepository clinicRepository,
                                UserRepository userRepository,
                                AppointmentRepository appointmentRepository,
                                KnowledgeArticleRepository knowledgeArticleRepository) {
        this.clinicRepository = clinicRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.knowledgeArticleRepository = knowledgeArticleRepository;
    }

    @Override
    public DashboardSummaryDto getSummary() {
        long doctors = userRepository.countByRoleIn(EnumSet.of(UserRole.DOCTOR, UserRole.PSYCHOLOGIST));
        long patients = userRepository.countByRoleIn(EnumSet.of(UserRole.PATIENT, UserRole.CLIENT));

        return new DashboardSummaryDto(
                clinicRepository.count(),
                doctors,
                patients,
                appointmentRepository.count(),
                knowledgeArticleRepository.count()
        );
    }
}