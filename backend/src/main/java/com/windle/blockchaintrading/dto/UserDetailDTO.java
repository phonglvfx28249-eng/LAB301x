package com.windle.blockchaintrading.dto;


import com.windle.blockchaintrading.entity.User;

import java.time.LocalDateTime;

/** Full detail shape for the "Detail" button on a user row. */
public class UserDetailDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String country;
    private String role;
    private String status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public static UserDetailDTO from(User u) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.fullName = u.getFullName();
        dto.role = u.getRole().name();
        dto.status = u.getStatus().name();
        dto.lastLogin = u.getLastLogin();
        dto.createdAt = u.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getCountry() { return country; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
