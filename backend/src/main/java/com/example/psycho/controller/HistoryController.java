package com.example.psycho.controller;

import com.example.psycho.dto.AuditEventResponseDto;
import com.example.psycho.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class HistoryController {
    private final AuditService auditService;

    public HistoryController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/events/entity/{entityName}/{entityId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<Page<AuditEventResponseDto>> byEntity(@PathVariable String entityName,
                                                                @PathVariable Long entityId,
                                                                Pageable pageable) {
        return ResponseEntity.ok(auditService.getByEntity(entityName, entityId, pageable));
    }

    @GetMapping("/actors/{actorId}/events")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST')")
    public ResponseEntity<Page<AuditEventResponseDto>> byActor(@PathVariable Long actorId,
                                                               Pageable pageable) {
        return ResponseEntity.ok(auditService.getByActor(actorId, pageable));
    }
}

