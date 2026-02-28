package com.example.psycho.repository;

import com.example.psycho.model.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<ClinicEntity, Long> {
    boolean existsByName(String name);
}

