package com.example.psycho.repository;

import com.example.psycho.model.AppointmentEntity;
import com.example.psycho.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    Page<AppointmentEntity> findByClient_Id(Long clientId, Pageable pageable);

    Page<AppointmentEntity> findByPsychologist_Id(Long psychologistId, Pageable pageable);

    List<AppointmentEntity> findByPsychologist_IdAndTimeBetween(Long psychologistId, LocalDateTime from, LocalDateTime to);

    List<AppointmentEntity> findByClinic_Id(Long clinicId);

    List<AppointmentEntity> findByClinic_IdOrderByTimeAsc(Long clinicId);

    boolean existsByPsychologist_IdAndTimeAndStatusIn(Long psychologistId, LocalDateTime time, Collection<AppointmentStatus> statuses);

    List<AppointmentEntity> findByPsychologist_IdAndTimeAndStatusIn(
            Long psychologistId,
            LocalDateTime time,
            Collection<AppointmentStatus> statuses
    );
}

