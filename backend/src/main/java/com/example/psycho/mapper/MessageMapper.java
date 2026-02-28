package com.example.psycho.mapper;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.model.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponseDto toDto(MessageEntity message) {
        return new MessageResponseDto(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getAppointment().getId(),
                message.getType(),
                message.getText(),
                message.getMetadata(),
                message.getTime()
        );
    }
}

