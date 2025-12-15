package com.chatassist.backend.service;

import com.chatassist.backend.dto.LoginRequest;
import com.chatassist.backend.dto.SignupRequest;
import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.UserRepository;
import com.chatassist.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setCreatedAt(Instant.now());
        userRepository.save(u);
    }

    public String login(LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        System.out.println("User from DB = " + userRepository.findByEmail(req.getEmail()));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        u.setLastLoginAt(Instant.now());
        userRepository.save(u);
        return jwtUtil.generateToken(u.getId().toString(), u.getEmail());
    }
}
