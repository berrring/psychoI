    package com.example.psycho.service;

    import com.example.psycho.dto.AppointmentRequestDto;
    import com.example.psycho.dto.AppointmentResponseDto;
    import com.example.psycho.entity.AppointmentEntity;
    import com.example.psycho.enums.AppointmentStatus;
    import com.example.psycho.repository.AppointmentRepository;
    import com.example.psycho.repository.UserRepository;
    import jakarta.persistence.EntityNotFoundException;
    import jakarta.transaction.Transactional;

    import org.springframework.stereotype.Service;
    import java.util.List;

    @Service
    public class AppointmentService {
        private final AppointmentRepository appointmentRepository;
        private final UserRepository userRepository;


        public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository) {
            this.appointmentRepository = appointmentRepository;
            this.userRepository = userRepository;

        }

        public AppointmentResponseDto getByID(Long id){
            AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Appointment not found id:" + id));
            return toResponse(appointmentEntity);

        }

        public AppointmentResponseDto createAppointment(AppointmentRequestDto request) {
            // 1. ПРОВЕРКА НА ЗАНЯТОСТЬ
            // Если существует запись у этого врача, на это время и она АКТИВНА (BOOKED) -> Ошибка
            boolean isBusy = appointmentRepository.existsByPsychologistIdAndTimeAndStatus(
                    request.psychologistId(),
                    request.time(),
                    AppointmentStatus.BOOKED
            );

            if (isBusy) {
                throw new RuntimeException("Извините, это время уже занято!");
            }

            // 2. Ищем участников
            var client = userRepository.findById(request.clientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            var psychologist = userRepository.findById(request.psychologistId())
                    .orElseThrow(() -> new RuntimeException("Psycho not found"));

            // 3. Создаем запись
            var appointmentToSave = new AppointmentEntity();
            appointmentToSave.setClient(client);
            appointmentToSave.setPsychologist(psychologist);
            appointmentToSave.setTime(request.time());
            appointmentToSave.setStatus(AppointmentStatus.BOOKED);

            var saved = appointmentRepository.save(appointmentToSave);
            return toResponse(saved);
        }

        @Transactional
        public void toCancelAppointment(Long id){
            if(!appointmentRepository.existsById(id)){
                throw new EntityNotFoundException("Appointment not found by id: " + id);
            }
            appointmentRepository.setStatus(id, AppointmentStatus.CANCELLED);

        }


        public AppointmentResponseDto appointmentUpdate(Long id,
                                                                  AppointmentResponseDto appointmenToUpdate) {
            AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException("Appointment not found by id: " + id));
            if(appointmenToUpdate.clientId() != null){
                var client = userRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException("Client not found by id: " + id));
                appointmentEntity.setClient(client);

            }
            if(appointmenToUpdate.psychologistId() != null){
                var psychologist = userRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Psychologist not found by id" + id));
                appointmentEntity.setPsychologist(psychologist);

            }
            if(appointmenToUpdate.time() != null){
                appointmentEntity.setTime(appointmenToUpdate.time());
            }

            var updated = appointmentRepository.save(appointmentEntity);
            return toResponse(updated);
        }


        public  List<AppointmentResponseDto> findAppointmentByClID(Long clientId) {
            return appointmentRepository.findAppointmentByClient_Id(clientId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        public List<AppointmentResponseDto> findAppointmentByPsId(Long psycholohistId) {
            return appointmentRepository.findAppointmentByPsychologist_Id(psycholohistId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        public AppointmentResponseDto toResponse(AppointmentEntity appointmentEntity){
            String clientName = appointmentEntity.getClient().getName();
            String psychologistName = appointmentEntity.getPsychologist().getName();
            return new AppointmentResponseDto(
                    appointmentEntity.getId(),
                    appointmentEntity.getClient().getId(),
                    clientName,
                    appointmentEntity.getPsychologist().getId(),
                    psychologistName,
                    appointmentEntity.getTime(),
                    appointmentEntity.getStatus()
            );
        }
    }

