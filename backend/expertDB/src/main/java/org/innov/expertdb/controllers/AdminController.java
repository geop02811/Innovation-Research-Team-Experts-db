package org.innov.expertdb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok("Expert Approved");
    }

    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id, @RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok("New Admin");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok("Deleted");
    }
}