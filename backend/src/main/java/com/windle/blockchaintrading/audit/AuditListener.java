package com.windle.blockchaintrading.audit;

import com.windle.blockchaintrading.entity.AuditLog;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.AuditLogRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.util.BeanUtil;
import jakarta.persistence.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

public class AuditListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        logAction(entity, "CREATE");
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        logAction(entity, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        logAction(entity, "DELETE");
    }

    private void logAction(Object entity, String action) {
        try {
            // 1. Prevent infinite recursion (don't log the logging table itself)
            if (entity instanceof AuditLog) {
                return;
            }

            // 2. Get Entity Name (e.g., "Trade", "Order", "Block")
            String entityName = entity.getClass().getSimpleName().toUpperCase();

            // 3. Extract Primary Key (entity_id) dynamically using Reflection
            Long entityId = getEntityId(entity);

            // 4. Fetch current user safely
            User currentUser = getCurrentUser();

            // 5. Construct the audit log
            AuditLog auditLog = new AuditLog(
                    currentUser,
                    action + "_" + entityName, // e.g., "CREATE_TRADE"
                    entityName,                // "TRADE"
                    entityId,                  // e.g., 105
                    action + " performed on " + entityName + " with ID: " + entityId
            );

            // 6. Fetch Spring Repository via BeanUtil and save
            AuditLogRepository auditLogRepository = BeanUtil.getBean(AuditLogRepository.class);
            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            // Fail safely so audit errors don't break main business logic execution
            e.printStackTrace();
        }
    }

    // Helper method to read the @Id field dynamically
    private Long getEntityId(Object entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    Object idValue = field.get(entity);
                    return idValue != null ? (Long) idValue : null;
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    // Safe retrieval of the authenticated user
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();

            // Case A: Principal is directly  custom User entity
            if (principal instanceof User) {
                return (User) principal;
            }

            // Case B: Principal is Spring Security's UserDetails
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
                return userRepository.findByUsername(username).orElse(null);
            }

            // Case C: Principal is a String (e.g., JWT subject or "anonymousUser")
            if (principal instanceof String) {
                String username = (String) principal;
                if ("anonymousUser".equals(username)) {
                    return null;
                }
                UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
                return userRepository.findByUsername(username).orElse(null);
            }

        } catch (Exception e) {
            // Log or ignore errors during auditing user lookup
            return null;
        }

        return null;
    }
}