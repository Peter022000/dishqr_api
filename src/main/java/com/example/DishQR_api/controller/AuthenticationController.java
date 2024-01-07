package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.ChangePasswordRequest;
import com.example.DishQR_api.dto.SignInRequest;
import com.example.DishQR_api.dto.SignUpRequest;
import com.example.DishQR_api.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

//    @PostMapping("/signup")
//    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
//        return authenticationService.signup(request);
//    }
//
//    @PostMapping("/signin")
//    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
//        return authenticationService.signin(request);
//    }

    @Operation(summary = "Register")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @Operation(summary = "Login in")
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    @Operation(summary = "Change password")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authenticationService.changePassword(changePasswordRequest);
    }
}
