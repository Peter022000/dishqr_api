package com.example.dishqr_api.controller;

import com.example.dishqr_api.dto.ChangePasswordRequest;
import com.example.dishqr_api.dto.SignInRequest;
import com.example.dishqr_api.dto.SignUpRequest;
import com.example.dishqr_api.service.AuthenticationService;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authenticationService.changePassword(changePasswordRequest);
    }
}
