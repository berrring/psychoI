package com.example.psycho.entity;

import com.example.psycho.enums.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //типы связи
    //клиет
    @ManyToOne
    @JoinColumn(name = "client_id")
    private UserEntity client;

    @ManyToOne
    @JoinColumn(name = "psychologist_id")
    private UserEntity psychologist;
    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;




    public AppointmentEntity() {
    }

    public AppointmentEntity(Long id, UserEntity client, UserEntity psychologist, LocalDateTime time, AppointmentStatus status) {
        this.id = id;
        this.client = client;
        this.psychologist = psychologist;
        this.time = time;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getClient() {
        return client;
    }

    public void setClient(UserEntity client) {
        this.client = client;
    }

    public UserEntity getPsychologist() {
        return psychologist;
    }

    public void setPsychologist(UserEntity psychologist) {
        this.psychologist = psychologist;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
