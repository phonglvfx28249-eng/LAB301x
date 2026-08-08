package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.TradeAdminDTO;
import com.windle.blockchaintrading.dto.WalletAdminDTO;
import com.windle.blockchaintrading.service.TradeAdminService;
import com.windle.blockchaintrading.service.WalletAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wallets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWalletController {

    private final WalletAdminService walletAdminService;
    private final TradeAdminService tradeAdminService;

    @Autowired
    public AdminWalletController(WalletAdminService walletAdminService,
                                  TradeAdminService tradeAdminService) {
        this.walletAdminService = walletAdminService;
        this.tradeAdminService = tradeAdminService;
    }

    @GetMapping
    public PageResponse<WalletAdminDTO> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        return walletAdminService.list(page, size, search);
    }

    /** Backs the "Trade History" link on each wallet row. */
    @GetMapping("/{userId}/trades")
    public PageResponse<TradeAdminDTO> tradeHistory(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return tradeAdminService.listForUser(userId, page, size);
    }
}
