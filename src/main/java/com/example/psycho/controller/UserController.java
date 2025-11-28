package com.example.psycho.controller;


import com.example.psycho.dto.UserResponseDto;
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
}
