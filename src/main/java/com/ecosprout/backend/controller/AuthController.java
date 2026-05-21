package com.ecosprout.backend.controller;

import com.ecosprout.backend.config.JwtUtil;
import com.ecosprout.backend.model.User;
import com.ecosprout.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // =========================
    // REGISTER USER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody Map<String, String> registrationRequest) {

        String username =
                registrationRequest.get("username");

        String email =
                registrationRequest.get("email");

        String password =
                registrationRequest.get("password");

        // VALIDATION
        if (username == null || email == null || password == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Username, email, and password are required.");
        }

        // CHECK USERNAME
        if (userRepository.findByUsername(username).isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username is already taken.");
        }

        // CHECK EMAIL
        if (userRepository.findByEmail(email).isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email is already in use.");
        }

        // CREATE USER
        User newUser = new User();

        newUser.setUsername(username);

        newUser.setEmail(email);

        // PASSWORD HASHING
        newUser.setPasswordHash(
                passwordEncoder.encode(password)
        );

        newUser.setCreatedAt(Instant.now());

        // DEFAULT ROLE
        newUser.setRole("ROLE_USER");

        // SAVE USER
        userRepository.save(newUser);

        return ResponseEntity.ok(
                "User registered successfully!"
        );
    }

    // =========================
    // LOGIN USER
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, String> loginRequest) {

        String username =
                loginRequest.get("username");

        String password =
                loginRequest.get("password");

        try {

            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );

        } catch (BadCredentialsException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }

        final UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        final String jwt =
                jwtUtil.generateToken(userDetails);

        List<String> roles =
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of(
                        "token", jwt,
                        "roles", roles
                )
        );
    }
}