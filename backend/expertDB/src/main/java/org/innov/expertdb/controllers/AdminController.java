package org.innov.expertdb.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.admin.AdminUserResponse;
import org.innov.expertdb.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        adminService.approveUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        adminService.rejectUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<Void> makeAdmin(@PathVariable UUID id) {
        adminService.makeAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        adminService.updateRole(id, body.get("role"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}