package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.dto.request.LoginRequest;
import com.windle.blockchaintrading.dto.request.RegisterRequest;
import com.windle.blockchaintrading.dto.response.AuthResponse;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.service.WalletService;
import com.windle.blockchaintrading.service.impl.UserServiceImpl;
import com.windle.blockchaintrading.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private WalletService walletService;

    @InjectMocks
    private AuthController authController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("encodedPass");
    }

    @Test
    void login_shouldReturnAuthResponseOnSuccess() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        when(userService.getUserByEmail("user@example.com")).thenReturn(sampleUser);
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com")).thenReturn("jwt.token.here");

        AuthResponse response = authController.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.token());
    }

    @Test
    void register_shouldReturnConflictIfEmailTaken() {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123");
        when(userService.isEmailTaken("user@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void register_shouldCreateUserAndWalletReturnJwt() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123");
        when(userService.isEmailTaken("new@example.com")).thenReturn(false);
        when(userService.getUserByEmail("new@example.com")).thenReturn(sampleUser);
        when(jwtUtil.generateToken("new@example.com")).thenReturn("jwt.token.new");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).registerUser("new@example.com", "new@example.com", "password123", "");
        verify(walletService).createWalletForUser(1L);
    }
}
