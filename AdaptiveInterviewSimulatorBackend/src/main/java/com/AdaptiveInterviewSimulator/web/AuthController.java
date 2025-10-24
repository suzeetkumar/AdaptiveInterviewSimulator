package com.AdaptiveInterviewSimulator.web;

import com.AdaptiveInterviewSimulator.dto.AuthResponse;
import com.AdaptiveInterviewSimulator.dto.LoginRequest;
import com.AdaptiveInterviewSimulator.dto.RegisterRequest;
import com.AdaptiveInterviewSimulator.model.User;
import com.AdaptiveInterviewSimulator.repo.UserRepository;
import com.AdaptiveInterviewSimulator.security.JwtUtil;
import com.AdaptiveInterviewSimulator.security.UserPrincipal;
import com.AdaptiveInterviewSimulator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        User saved = userService.registerNewUser(body);
        return ResponseEntity.ok().body("User registered with id: " + saved.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword())
            );
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            String jwt = jwtUtil.generateToken(principal.getUsername());
            return ResponseEntity.ok(jwt);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // example
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        UserPrincipal p = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findByEmail(p.getUsername())
                .map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
