package com.example.DishQR_api.service;

import com.example.DishQR_api.dto.JwtAuthenticationResponse;
import com.example.DishQR_api.dto.SignInRequest;
import com.example.DishQR_api.dto.SignUpRequest;
import com.example.DishQR_api.model.Role;
import com.example.DishQR_api.model.User;
import com.example.DishQR_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    public JwtAuthenticationResponse signup(SignUpRequest request) {
//        var user = User
//                .builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.ROLE_USER)
//                .build();
//
//        User = userService.save(user);
//        String jwt = jwtService.generateToken(user);
//        return JwtAuthenticationResponse.builder().token(jwt).build();
//    }
//
//    public JwtAuthenticationResponse signin(SignInRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
//        String jwt = jwtService.generateToken(user);
//        return JwtAuthenticationResponse.builder().token(jwt).build();
//    }

    public ResponseEntity<?> signup(SignUpRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email taken");
        }

        User user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        user = userService.save(user);
        String jwt = jwtService.generateToken(user);
        return ResponseEntity.status(201).body(JwtAuthenticationResponse.builder().token(jwt).build());
    }

    public ResponseEntity<?> signin(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
            var jwt = jwtService.generateToken(user);
            return ResponseEntity.ok(JwtAuthenticationResponse.builder().token(jwt).build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}
