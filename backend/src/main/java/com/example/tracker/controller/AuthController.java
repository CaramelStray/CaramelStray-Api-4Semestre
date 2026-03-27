package com.example.tracker.controller;

import com.example.tracker.security.JwtUtils;
import com.example.tracker.security.Login;
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

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/login")
    public ResponseEntity<Login> autenticar(@RequestBody Login login) {
        try {

            Authentication auth = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
            Authentication authResult = authManager.authenticate(auth);

            String token = JwtUtils.generateToken(authResult);
            
            login.setPassword(null);
            login.setToken(token);
            
            if (!authResult.getAuthorities().isEmpty()) {
                login.setAuth(authResult.getAuthorities().iterator().next().getAuthority());
            }

            return ResponseEntity.ok(login);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
