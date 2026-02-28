package com.example.psycho.repository;

import com.example.psycho.model.MedicalServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalServiceRepository extends JpaRepository<MedicalServiceEntity, Long> {
    List<MedicalServiceEntity> findByDepartment_Id(Long departmentId);

    boolean existsByCode(String code);

    Optional<MedicalServiceEntity> findByCode(String code);
}

