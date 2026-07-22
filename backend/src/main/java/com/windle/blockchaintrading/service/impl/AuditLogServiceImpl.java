package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.AuditLog;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.AuditLogRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void logAction(Long userId, String action, String entityName, Long entityId, String description, String ipAddress) {
        AuditLog log = new AuditLog();

        // userId is optional - system-triggered actions may not have an acting user
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            log.setUser(user);
        }

        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);

        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public AuditLog getLogById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<AuditLog> getLogsByUserId(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<AuditLog> getLogsByEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }
}