package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.UserAdminDTO;
import com.windle.blockchaintrading.dto.UserDetailDTO;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("admin");
        sampleUser.setEmail("admin@example.com");
    }

    @Test
    void list_shouldReturnPaginatedUserAdminDTO() {
        Page<User> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.search(eq("admin"), any(PageRequest.class))).thenReturn(page);

        PageResponse<UserAdminDTO> response = userAdminService.list(0, 10, "admin");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void getDetail_shouldReturnUserDetailDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserDetailDTO dto = userAdminService.getDetail(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userAdminService.delete(99L));
    }

    @Test
    void delete_shouldDeleteByIdWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userAdminService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
