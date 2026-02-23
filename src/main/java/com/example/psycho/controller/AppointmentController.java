package com.example.psycho.controller;


import com.example.psycho.dto.AppointmentRequestDto;
import com.example.psycho.dto.AppointmentResponseDto;
import com.example.psycho.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController{


    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(
            @RequestBody AppointmentRequestDto appointmentToCreate
    ){
        return ResponseEntity.ok(appointmentService.createAppointment(appointmentToCreate));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<AppointmentResponseDto> getAppointmentId(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(appointmentService.getByID(id));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('PSYCHOLOGIST')")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
        @PathVariable Long id,
        @RequestBody AppointmentResponseDto appointmenToUpdate
    ){
        var updated = appointmentService.appointmentUpdate(id, appointmenToUpdate);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id
    ){
        appointmentService.toCancelAppointment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('PSYCHOLOGIST')")
    public ResponseEntity<List<AppointmentResponseDto>> findByClientId(
            @PathVariable Long clientId
    ){
        return ResponseEntity.ok(appointmentService.findAppointmentByClID(clientId));
    }

    @GetMapping("/psychologist/{psychologistId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<List<AppointmentResponseDto>> findPsychologistId(
            @PathVariable Long psychologistId
    ){
        return ResponseEntity.ok(appointmentService.findAppointmentByPsId(psychologistId));
    }



}
