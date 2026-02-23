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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(userService.getUserId(id));
    }

    @GetMapping("/psychologists")
    @PreAuthorize("hasAuthority('CLIENT')") // Только клиенты ищут психологов
    public ResponseEntity<Page<UserResponseDto>> getPsychologists(Pageable pageable) {
        return ResponseEntity.ok(userService.getPsychologists(pageable));
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('PSYCHOLOGIST')") // Только психологи видят список клиентов
    public ResponseEntity<Page<UserResponseDto>> getClients(Pageable pageable) {
        return ResponseEntity.ok(userService.getClients(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<UserResponseDto> updateUserName(
            @PathVariable Long id,
            @RequestBody UserUpdateDto updateDto
    ){
        return ResponseEntity.ok(userService.updateUserName(id, updateDto));
    }

    @PatchMapping("/change-password/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'PSYCHOLOGIST')")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequestDto request
    ) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build(); // Возвращаем 200 OK без тела
    }
}
