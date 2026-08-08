package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.BlockAdminDTO;
import com.windle.blockchaintrading.service.BlockchainAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/blockchain")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlockchainController {

    private final BlockchainAdminService blockchainAdminService;

    @Autowired
    public AdminBlockchainController(BlockchainAdminService blockchainAdminService) {
        this.blockchainAdminService = blockchainAdminService;
    }

    @GetMapping("/blocks")
    public PageResponse<BlockAdminDTO> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return blockchainAdminService.list(page, size);
    }
}
