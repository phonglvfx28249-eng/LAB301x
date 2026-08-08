package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.AuditLogDTO;
import com.windle.blockchaintrading.entity.AuditLog;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.AuditLogRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("admin");
    }

    @Test
    void logAction_shouldSaveAuditLogWithUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        auditLogService.logAction(1L, "CREATE", "Order", 100L, "Created Order", "127.0.0.1");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals("CREATE", saved.getAction());
        assertEquals("Order", saved.getEntityName());
        assertEquals(sampleUser, saved.getUser());
    }

    @Test
    void logAction_shouldSaveSystemAuditLogWhenUserIdNull() {
        auditLogService.logAction(null, "SYSTEM_BATCH", "Block", 5L, "Forged Block", "127.0.0.1");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertNull(saved.getUser());
        assertEquals("SYSTEM_BATCH", saved.getAction());
    }

    @Test
    void list_shouldReturnPageResponse() {
        AuditLog log = new AuditLog();
        log.setId(10L);
        log.setAction("LOGIN");
        Page<AuditLog> page = new PageImpl<>(List.of(log));

        when(auditLogRepository.search(eq("test"), any(PageRequest.class))).thenReturn(page);

        PageResponse<AuditLogDTO> response = auditLogService.list(0, 10, "test");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }
}
