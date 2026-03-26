package org.innov.expertdb.controllers;

import lombok.RequiredArgsConstructor;
import org.innov.expertdb.dtos.UserCreateRequest;
import org.innov.expertdb.dtos.UserLoginRequest;
import org.innov.expertdb.dtos.UserResponse;
import org.innov.expertdb.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> userLogIn(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.userLogIn(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}