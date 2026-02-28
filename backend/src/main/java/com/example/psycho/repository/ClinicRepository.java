package com.example.psycho.repository;

import com.example.psycho.model.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<ClinicEntity, Long> {
    boolean existsByName(String name);
    Optional<ClinicEntity> findByName(String name);
}

