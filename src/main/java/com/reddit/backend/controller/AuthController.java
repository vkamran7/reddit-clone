package com.reddit.backend.controller;

import com.reddit.backend.dto.RegisterRequest;
import com.reddit.backend.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return new ResponseEntity<>("Account created, but needs activation",HttpStatus.OK);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verify(@PathVariable String token) {
        authService.verifyToken(token);
        return new ResponseEntity<>("Account activated",HttpStatus.OK);
    }
}
