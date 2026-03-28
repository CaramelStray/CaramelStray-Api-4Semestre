package com.example.tracker.controller;

import com.example.tracker.dto.auth.LoginRequestDTO;
import com.example.tracker.dto.auth.LoginResponseDTO;
import com.example.tracker.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO login) {
        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
            Authentication authResult = authManager.authenticate(auth);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setEmail(authResult.getName());
            response.setToken(jwtService.generateToken(authResult));

            if (!authResult.getAuthorities().isEmpty()) {
                response.setAuth(authResult.getAuthorities().iterator().next().getAuthority());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Falha ao autenticar usuario {}", login.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
