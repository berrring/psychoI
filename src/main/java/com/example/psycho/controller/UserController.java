package com.example.psycho.controller;


import com.example.psycho.dto.ChangePasswordRequestDto;
import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.dto.UserUpdateDto;
import com.example.psycho.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(userService.getUserId(id));
    }
    @GetMapping("/psychologists")
    public ResponseEntity<List<UserResponseDto>> getAllPsychologists() {
        return ResponseEntity.ok(userService.getAllPsychologists());
    }

    @GetMapping("/clients")
    public ResponseEntity<List<UserResponseDto>> getAllClients() {
        return ResponseEntity.ok(userService.getAllClients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserName(
            @PathVariable Long id,
            @RequestBody UserUpdateDto updateDto
    ){
        return ResponseEntity.ok(userService.updateUserName(id, updateDto));
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequestDto request
    ) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build(); // Возвращаем 200 OK без тела
    }
}
