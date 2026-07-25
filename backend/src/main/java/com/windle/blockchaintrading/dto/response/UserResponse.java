package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.User;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        User.Role role,
        User.Status status,
        LocalDateTime lastLogin,
        LocalDateTime createdAt
) {
    // Convenience constructor mapping from the User entity directly
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getStatus(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
}