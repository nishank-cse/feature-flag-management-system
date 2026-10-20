package com.github.nishank.featureflags.service;

import com.github.nishank.featureflags.dto.AuthResponse;
import com.github.nishank.featureflags.dto.LoginRequest;
import com.github.nishank.featureflags.dto.RegisterRequest;
import com.github.nishank.featureflags.entity.Role;
import com.github.nishank.featureflags.entity.User;
import com.github.nishank.featureflags.repository.UserRepository;
import com.github.nishank.featureflags.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // First user becomes ADMIN, all others are GUEST
        boolean isFirstUser = userRepository.count() == 0;
        Role assignedRole = isFirstUser ? Role.ADMIN : Role.GUEST;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(assignedRole)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user);

        return AuthResponse.of(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtUtil.generateToken(user);

        return AuthResponse.of(token, user);
    }
}
