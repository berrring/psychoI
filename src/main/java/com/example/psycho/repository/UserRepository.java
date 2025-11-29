package com.example.psycho.repository;

import com.example.psycho.entity.UserEntity;
import com.example.psycho.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByRole(UserRole role);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

}
