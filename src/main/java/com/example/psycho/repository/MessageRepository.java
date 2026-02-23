package com.example.psycho.repository;

import com.example.psycho.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Page<MessageEntity> findByAppointment_IdOrderByTimeAsc(Long appointmentId, Pageable pageable);

}
