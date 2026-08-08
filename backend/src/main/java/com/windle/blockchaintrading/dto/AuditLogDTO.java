package com.windle.blockchaintrading.dto;

import com.windle.blockchaintrading.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogDTO {

    private Long id;
    private Long userId;
    private String action;
    private String entityName;
    private Long entityId;
    private String description;
    private String ipAddress;
    private LocalDateTime createdAt;

    public static AuditLogDTO from(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.id = log.getId();
        dto.userId = log.getUser().getId();
        dto.action = log.getAction();
        dto.entityName = log.getEntityName();
        dto.entityId = log.getEntityId();
        dto.description = log.getDescription();
        dto.ipAddress = log.getIpAddress();
        dto.createdAt = log.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAction() { return action; }
    public String getEntityName() { return entityName; }
    public Long getEntityId() { return entityId; }
    public String getDescription() { return description; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
