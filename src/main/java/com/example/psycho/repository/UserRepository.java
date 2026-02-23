package com.example.psycho.repository;

import com.example.psycho.entity.UserEntity;
import com.example.psycho.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

}
