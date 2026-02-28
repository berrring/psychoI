package com.example.psycho.service;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    MessageResponseDto findMessageById(Long id);

    MessageResponseDto sendMessage(MessageSendRequestDto messageToSend, String actorEmail);

    Page<MessageResponseDto> findByAppointmentId(Long appointmentId, Pageable pageable);

    List<MessageResponseDto> findTimelineByAppointmentId(Long appointmentId);
}

