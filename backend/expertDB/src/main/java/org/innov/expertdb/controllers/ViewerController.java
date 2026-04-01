package org.innov.expertdb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/viewer")
public class ViewerController {

    @GetMapping("/my-profile")
    public ResponseEntity<String> viewProfile() {
        return ResponseEntity.ok("My Profile");
    }

    @GetMapping("/find-expert")
    public ResponseEntity<String> findExpert() {
        return ResponseEntity.ok("My Expert");
    }
}
