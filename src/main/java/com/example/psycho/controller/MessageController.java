package com.example.psycho.controller;


import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.entity.MessageEntity;
import com.example.psycho.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("{id}")
    public ResponseEntity<MessageResponseDto> getMessageById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(messageService.findMessageById(id));
        
    }
    //отправка сообщения
    @PostMapping()
    public ResponseEntity<MessageResponseDto> sendMessage(
        @RequestBody MessageSendRequestDto messageToSend
    ){
        return ResponseEntity.ok(messageService.sendMessage(messageToSend));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByAppointmentById(
            @PathVariable Long appointmentId
    ){
        return ResponseEntity.ok(messageService.findMessageByAppointmentId(appointmentId));

    }
}
