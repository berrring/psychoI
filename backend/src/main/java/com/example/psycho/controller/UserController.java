package com.example.psycho.controller;

import com.example.psycho.dto.ChangePasswordRequestDto;
import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.dto.UserUpdateDto;
import com.example.psycho.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<Page<UserResponseDto>> getDoctors(@RequestParam(required = false) String query,
                                                             Pageable pageable) {
        return ResponseEntity.ok(userService.getDoctors(query, pageable));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<Page<UserResponseDto>> getPatients(Pageable pageable) {
        return ResponseEntity.ok(userService.getPatients(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/change-password")
    @PreAuthorize("hasAnyAuthority('ADMIN','RECEPTIONIST','DOCTOR','PSYCHOLOGIST','PATIENT','CLIENT')")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}

