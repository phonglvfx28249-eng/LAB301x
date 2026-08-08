package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.AuditLogDTO;
import com.windle.blockchaintrading.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public AdminAuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public PageResponse<AuditLogDTO> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {

        System.out.println("AdminAuditLogController.list called with page=" + page + ", size=" + size + ", search=" + search);
        return auditLogService.list(page, size, search);
    }
}
