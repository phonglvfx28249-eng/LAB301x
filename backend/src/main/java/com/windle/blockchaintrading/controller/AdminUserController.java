package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.UserAdminDTO;
import com.windle.blockchaintrading.dto.UserDetailDTO;
import com.windle.blockchaintrading.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserAdminService userAdminService;

    @Autowired
    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public PageResponse<UserAdminDTO> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        return userAdminService.list(page, size, search);
    }

    @GetMapping("/{id}")
    public UserDetailDTO detail(@PathVariable Long id) {
        return userAdminService.getDetail(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userAdminService.delete(id);
    }
}
