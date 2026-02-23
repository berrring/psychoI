package com.example.psycho.controller;


import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<MessageResponseDto> getMessageById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(messageService.findMessageById(id));
        
    }
    //отправка сообщения
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<MessageResponseDto> sendMessage(
        @RequestBody MessageSendRequestDto messageToSend
    ){
        return ResponseEntity.ok(messageService.sendMessage(messageToSend));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<Page<MessageResponseDto>> getMessagesByAppointmentById(
            @PathVariable Long appointmentId,
            Pageable pageable
    ){
        return ResponseEntity.ok(messageService.findMessageByAppointmentId(appointmentId, pageable));

    }
}
