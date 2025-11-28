package com.example.psycho.repository;

import com.example.psycho.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByAppointment_IdOrderByTimeAsc(Long appointmentId);

}
