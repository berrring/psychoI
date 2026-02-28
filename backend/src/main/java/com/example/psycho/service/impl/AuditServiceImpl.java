package com.example.psycho.service.impl;

import com.example.psycho.dto.AuditEventResponseDto;
import com.example.psycho.model.AuditEventEntity;
import com.example.psycho.model.UserEntity;
import com.example.psycho.mapper.AuditMapper;
import com.example.psycho.model.AuditAction;
import com.example.psycho.repository.AuditEventRepository;
import com.example.psycho.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {
    private final AuditEventRepository auditEventRepository;
    private final AuditMapper auditMapper;

    public AuditServiceImpl(AuditEventRepository auditEventRepository, AuditMapper auditMapper) {
        this.auditEventRepository = auditEventRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    public void log(String entityName, Long entityId, AuditAction action, UserEntity actor, String details) {
        if (actor == null || entityId == null || entityName == null || action == null) {
            return;
        }

        AuditEventEntity event = new AuditEventEntity();
        event.setEntityName(entityName);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setActorId(actor.getId());
        event.setActorEmail(actor.getEmail());
        event.setDetails(details);
        auditEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponseDto> getByEntity(String entityName, Long entityId, Pageable pageable) {
        return auditEventRepository.findByEntityNameAndEntityIdOrderByCreatedAtDesc(entityName, entityId, pageable)
                .map(auditMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponseDto> getByActor(Long actorId, Pageable pageable) {
        return auditEventRepository.findByActorIdOrderByCreatedAtDesc(actorId, pageable)
                .map(auditMapper::toDto);
    }
}

