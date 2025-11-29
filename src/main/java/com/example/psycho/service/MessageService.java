package com.example.psycho.service;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.entity.AppointmentEntity;
import com.example.psycho.entity.MessageEntity;
import com.example.psycho.model.AppointmentStatus;
import com.example.psycho.repository.AppointmentRepository;
import com.example.psycho.repository.MessageRepository;
import com.example.psycho.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final AppointmentRepository appointmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(AppointmentRepository appointmentRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponseDto findMessageById(Long id) {
        MessageEntity messageEntity = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Messages not found by id: " + id));
        return toResponseMessage(messageEntity);
    }

    public MessageResponseDto sendMessage(MessageSendRequestDto messageToSend) {
        var appointment = appointmentRepository.findById(messageToSend.appointmentId())
                        .orElseThrow(()-> new EntityNotFoundException("Appointment not found by id"));
        var sender = userRepository.findById(messageToSend.senderId())
                       .orElseThrow(()-> new EntityNotFoundException("Sender not found by id"));
        checkChatAccess(appointment, messageToSend.senderId());
        var newMessage = new MessageEntity(
                    appointment,
                    sender,
                    messageToSend.text(),
                    LocalDateTime.now()
              );
        var saved = messageRepository.save(newMessage);
        return toResponseMessage(saved);

    }
    private void checkChatAccess(AppointmentEntity appointment,
                                 Long senderId){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = appointment.getTime();
        LocalDateTime endTime = startTime.plusMinutes(60);
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            // Дополнительно можно проверить статус: не должно быть CANCELLED
            if (appointment.getStatus() != AppointmentStatus.BOOKED) {
                throw new IllegalStateException("Appointment is not active.");
            }
            throw new IllegalStateException("Chat is only available during the appointment window: "
                    + startTime + " to " + endTime);
        }

        // Проверка прав: Отправитель должен быть либо клиентом, либо психологом в этой записи
        if (!appointment.getClient().getId().equals(senderId) &&
                !appointment.getPsychologist().getId().equals(senderId)) {
            throw new SecurityException("User does not belong to this appointment.");
        }
    }

    public List<MessageResponseDto> findMessageByAppointmentId(Long appointmentId) {
        return messageRepository.findByAppointment_IdOrderByTimeAsc(appointmentId)
                .stream()
                .map(this::toResponseMessage)
                .toList();

    }
    public MessageResponseDto toResponseMessage(MessageEntity messageEntity){
        String senderName = messageEntity.getSender().getName();
        return new MessageResponseDto(
                messageEntity.getId(),
                messageEntity.getSender().getId(), // Сначала senderId
                senderName,
                messageEntity.getAppointment().getId(), // Потом appointmentId
                messageEntity.getText(),
                messageEntity.getTime()
        );
    }
}
