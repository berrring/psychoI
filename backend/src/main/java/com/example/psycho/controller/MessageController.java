package com.example.psycho.controller;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.model.UserEntity;
import com.example.psycho.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/appointments/{appointmentId}/events")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<MessageResponseDto> send(@PathVariable Long appointmentId,
                                                   @Valid @RequestBody MessageSendRequestDto request,
                                                   @AuthenticationPrincipal UserEntity actor) {
        MessageSendRequestDto normalized = new MessageSendRequestDto(
                request.senderId(),
                appointmentId,
                request.type(),
                request.text(),
                request.metadata()
        );
        return ResponseEntity.ok(messageService.sendMessage(normalized, actor.getEmail()));
    }

    @GetMapping("/appointments/{appointmentId}/events")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<Page<MessageResponseDto>> byAppointment(@PathVariable Long appointmentId,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(messageService.findByAppointmentId(appointmentId, pageable));
    }

    @GetMapping("/appointments/{appointmentId}/events/timeline")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<List<MessageResponseDto>> timeline(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(messageService.findTimelineByAppointmentId(appointmentId));
    }

    @GetMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<MessageResponseDto> getByIdV1(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findMessageById(id));
    }
}

