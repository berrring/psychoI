package com.example.psycho.repository;

import com.example.psycho.model.UserEntity;
import com.example.psycho.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    Page<UserEntity> findByRoleIn(Collection<UserRole> roles, Pageable pageable);

    long countByRoleIn(Collection<UserRole> roles);

    @Query("""
            select u from UserEntity u
            where u.role in :roles
              and u.active = true
              and (
                    :query is null
                    or trim(:query) = ''
                    or lower(u.name) like lower(concat('%', :query, '%'))
                    or lower(coalesce(u.specialization, '')) like lower(concat('%', :query, '%'))
                  )
            """)
    Page<UserEntity> searchActiveByRolesAndQuery(
            @Param("roles") Collection<UserRole> roles,
            @Param("query") String query,
            Pageable pageable
    );
}

