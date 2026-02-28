package com.example.psycho.service;

import com.example.psycho.dto.AuditEventResponseDto;
import com.example.psycho.model.UserEntity;
import com.example.psycho.model.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    void log(String entityName, Long entityId, AuditAction action, UserEntity actor, String details);

    Page<AuditEventResponseDto> getByEntity(String entityName, Long entityId, Pageable pageable);

    Page<AuditEventResponseDto> getByActor(Long actorId, Pageable pageable);
}

