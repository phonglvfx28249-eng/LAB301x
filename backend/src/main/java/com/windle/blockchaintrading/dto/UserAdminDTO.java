package com.windle.blockchaintrading.dto;


import com.windle.blockchaintrading.entity.User;

/** Row shape for the "User management" table. */
public class UserAdminDTO {

    private Long id;
    private String username;
    private String email;
    private String country;
    private String role;
    private String status;

    public static UserAdminDTO from(User u) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.role = u.getRole().name();
        dto.status = u.getStatus().name();
        return dto;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getCountry() { return country; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}
