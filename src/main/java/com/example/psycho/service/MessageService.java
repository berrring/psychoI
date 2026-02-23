package com.example.psycho.service;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.entity.AppointmentEntity;
import com.example.psycho.entity.MessageEntity;
import com.example.psycho.repository.AppointmentRepository;
import com.example.psycho.repository.MessageRepository;
import com.example.psycho.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private final AppointmentRepository appointmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(AppointmentRepository appointmentRepository, MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
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
        
        // TODO: Uncomment access check in production
        //checkChatAccess(appointment, messageToSend.senderId());
        
        var newMessage = new MessageEntity(
                    appointment,
                    sender,
                    messageToSend.text(),
                    LocalDateTime.now()
              );
        var saved = messageRepository.save(newMessage);
        MessageResponseDto responseDto = toResponseMessage(saved);
        
        // Push to WebSocket topic for real-time updates
        messagingTemplate.convertAndSend("/topic/appointment/" + saved.getAppointment().getId(), responseDto);
        
        return responseDto;
    }

    private void checkChatAccess(AppointmentEntity appointment, Long senderId) {
        LocalDateTime now = LocalDateTime.now(); // Время сервера (UTC)
        LocalDateTime startTime = appointment.getTime();

        // Расширяем окно доступа (-5 часов и +6 часов), чтобы компенсировать разницу часовых поясов (Бишкек vs UTC)
        LocalDateTime accessStart = startTime.minusHours(5);
        LocalDateTime accessEnd = startTime.plusHours(6);

        if (now.isBefore(accessStart) || now.isAfter(accessEnd)) {
            // Если время сервера не попадает в расширенное окно — запрещаем доступ
            throw new IllegalStateException("Чат недоступен. Окно доступа: с " + accessStart + " по " + accessEnd);
        }

        // Проверка: отправитель должен быть участником записи
        if (!appointment.getClient().getId().equals(senderId) &&
                !appointment.getPsychologist().getId().equals(senderId)) {
            throw new SecurityException("User does not belong to this appointment.");
        }
    }

    public Page<MessageResponseDto> findMessageByAppointmentId(Long appointmentId, Pageable pageable) {
        return messageRepository.findByAppointment_IdOrderByTimeAsc(appointmentId, pageable)
                .map(this::toResponseMessage);
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
