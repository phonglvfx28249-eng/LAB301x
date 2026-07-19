package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.dto.request.LoginRequest;
import com.windle.blockchaintrading.dto.request.RegisterRequest;
import com.windle.blockchaintrading.dto.response.AuthResponse;
import com.windle.blockchaintrading.dto.response.ErrorResponse;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.service.impl.UserServiceImpl;
import com.windle.blockchaintrading.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {


    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );


        // generate email token
        String token = jwtUtil.generateToken(request.email());
        return new AuthResponse(token);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {


        // check if email already exists
        if (userService.isEmailTaken(request.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Email is already registered"));
        }

        //save to DB
        userService.registerUser("", request.email(), passwordEncoder.encode(request.password()), "");

        // response with JWT
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }
}
