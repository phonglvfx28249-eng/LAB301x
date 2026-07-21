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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
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

        // 1. Print what the frontend just sent you
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Frontend Raw Password: [" + request.password() + "]");

        // 2. Fetch the user manually to see what's actually in the DB
        try {
            UserDetails user = userService.getUserByEmail(request.email());
            System.out.println("Database Encrypted Hash: [" + user.getPassword() + "]");

            // 3. Test the encoder manually right here
            boolean manuallyMatches = passwordEncoder.matches(request.password(), user.getPassword());
            System.out.println("Does encoder say they match?: " + manuallyMatches);
        } catch (Exception e) {
            System.out.println("Could not even find user: " + e.getMessage());
        }
        System.out.println("===================");


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception e) {
            System.out.println("Authentication failed explicitly because: " + e.getMessage());
            e.printStackTrace(); // This will show you exactly if it's BadCredentialsException or something else
            throw e;
        }

        System.out.println("User " + request.email() + " logged in successfully.");
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
        userService.registerUser("", request.email(), request.password(), "");

        // response with JWT
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
