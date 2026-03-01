package com.example.psycho.service;

import com.example.psycho.mapper.UserMapper;
import com.example.psycho.repository.ClinicRepository;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, clinicRepository, passwordEncoder, new UserMapper());
    }

    @Test
    void getDoctorsShouldNormalizeNullQueryToEmptyString() {
        when(userRepository.searchActiveByRolesAndQuery(anyCollection(), eq(""), any()))
                .thenReturn(Page.empty());

        userService.getDoctors(null, PageRequest.of(0, 10));

        verify(userRepository).searchActiveByRolesAndQuery(anyCollection(), eq(""), any());
    }

    @Test
    void getDoctorsShouldTrimIncomingQuery() {
        when(userRepository.searchActiveByRolesAndQuery(anyCollection(), eq("alex"), any()))
                .thenReturn(Page.empty());

        userService.getDoctors("  alex  ", PageRequest.of(0, 10));

        verify(userRepository).searchActiveByRolesAndQuery(anyCollection(), eq("alex"), any());
    }
}
