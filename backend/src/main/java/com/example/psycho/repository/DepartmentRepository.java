package com.example.psycho.repository;

import com.example.psycho.model.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    List<DepartmentEntity> findByClinic_Id(Long clinicId);

    Optional<DepartmentEntity> findByClinic_IdAndName(Long clinicId, String name);
}

