package com.example.psycho.service.impl;

import com.example.psycho.dto.MessageResponseDto;
import com.example.psycho.dto.MessageSendRequestDto;
import com.example.psycho.exception.ForbiddenOperationException;
import com.example.psycho.model.AppointmentEntity;
import com.example.psycho.model.MessageEntity;
import com.example.psycho.model.UserEntity;
import com.example.psycho.mapper.MessageMapper;
import com.example.psycho.model.AuditAction;
import com.example.psycho.model.MessageType;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.AppointmentRepository;
import com.example.psycho.repository.MessageRepository;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.AuditService;
import com.example.psycho.service.IdentityService;
import com.example.psycho.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private static final Set<UserRole> STAFF_ROLES = EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR, UserRole.PSYCHOLOGIST);

    private final AppointmentRepository appointmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final AuditService auditService;
    private final IdentityService identityService;

    public MessageServiceImpl(AppointmentRepository appointmentRepository,
                              MessageRepository messageRepository,
                              UserRepository userRepository,
                              MessageMapper messageMapper,
                              AuditService auditService,
                              IdentityService identityService) {
        this.appointmentRepository = appointmentRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.auditService = auditService;
        this.identityService = identityService;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponseDto findMessageById(Long id) {
        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message not found by id: " + id));
        return messageMapper.toDto(message);
    }

    @Override
    public MessageResponseDto sendMessage(MessageSendRequestDto messageToSend, String actorEmail) {
        UserEntity actor = identityService.requireByEmail(actorEmail);
        AppointmentEntity appointment = appointmentRepository.findById(messageToSend.appointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found by id: " + messageToSend.appointmentId()));
        UserEntity sender = userRepository.findById(messageToSend.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found by id: " + messageToSend.senderId()));

        if (!actor.getId().equals(sender.getId()) && !STAFF_ROLES.contains(actor.getRole())) {
            throw new ForbiddenOperationException("You cannot send event on behalf of another user");
        }

        if (!sender.getId().equals(appointment.getClient().getId())
                && !sender.getId().equals(appointment.getPsychologist().getId())
                && !STAFF_ROLES.contains(sender.getRole())) {
            throw new ForbiddenOperationException("Sender does not belong to this appointment");
        }

        MessageEntity message = new MessageEntity();
        message.setAppointment(appointment);
        message.setSender(sender);
        message.setType(messageToSend.type() != null ? messageToSend.type() : MessageType.NOTE);
        message.setText(messageToSend.text());
        message.setMetadata(messageToSend.metadata());

        MessageEntity saved = messageRepository.save(message);

        auditService.log("appointments", appointment.getId(), AuditAction.UPDATE, actor,
                "Added appointment event type=" + saved.getType());

        return messageMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseDto> findByAppointmentId(Long appointmentId, Pageable pageable) {
        return messageRepository.findByAppointment_IdOrderByTimeAsc(appointmentId, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> findTimelineByAppointmentId(Long appointmentId) {
        return messageRepository.findByAppointment_IdOrderByTimeAsc(appointmentId)
                .stream()
                .map(messageMapper::toDto)
                .toList();
    }
}

