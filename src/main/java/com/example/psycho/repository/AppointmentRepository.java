package com.example.psycho.repository;

import com.example.psycho.entity.AppointmentEntity;
import com.example.psycho.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>{
    List<AppointmentEntity> findAppointmentByClient_Id(Long clientId);
    List<AppointmentEntity> findAppointmentByPsychologist_Id(Long psychologistId);

    boolean existsByPsychologistIdAndTimeAndStatus(Long psychologistId, LocalDateTime time, AppointmentStatus status);

    @Modifying
    @Query("update AppointmentEntity r set r.status = :status where r.id = :id")
    void setStatus(@Param("id") Long id,
                   @Param("status") AppointmentStatus status);
}
