package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setFullName("Test User");
    }

    @Test
    void isUsernameTaken_shouldReturnTrueWhenExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userService.isUsernameTaken("testuser"));
    }

    @Test
    void isEmailTaken_shouldReturnTrueWhenExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(userService.isEmailTaken("test@example.com"));
    }

    @Test
    void registerUser_shouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");

        userService.registerUser("newuser", "new@example.com", "rawPass", "New User");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encodedPass", savedUser.getPassword());
        assertEquals("New User", savedUser.getFullName());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        User result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_shouldReturnUserWhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_shouldReturnNullWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.getUserById(99L);

        assertNull(result);
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        userService.updateUser(1L, "updatedUser", "updated@example.com", "Updated Name");

        verify(userRepository).save(sampleUser);
        assertEquals("updatedUser", sampleUser.getUsername());
        assertEquals("updated@example.com", sampleUser.getEmail());
        assertEquals("Updated Name", sampleUser.getFullName());
    }

    @Test
    void deleteUser_shouldInvokeRepositoryDelete() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void updatePassword_shouldEncodeNewPasswordAndSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.updatePassword("newPassword", 1L);

        verify(userRepository).save(sampleUser);
        assertEquals("newEncodedPassword", sampleUser.getPassword());
    }
}
