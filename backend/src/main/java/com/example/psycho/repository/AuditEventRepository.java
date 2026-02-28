package com.example.psycho.repository;

import com.example.psycho.model.AuditEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
    Page<AuditEventEntity> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, Long entityId, Pageable pageable);

    Page<AuditEventEntity> findByActorIdOrderByCreatedAtDesc(Long actorId, Pageable pageable);
}

