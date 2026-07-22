package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.AuditLog;

import java.util.List;

public interface AuditLogService {

    void logAction(Long userId, String action, String entityName, Long entityId, String description, String ipAddress);

    List<AuditLog> getAllLogs();

    AuditLog getLogById(Long id);

    List<AuditLog> getLogsByUserId(Long userId);

    List<AuditLog> getLogsByEntity(String entityName, Long entityId);
}
