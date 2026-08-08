package com.windle.blockchaintrading.dto;

import java.math.BigDecimal;


public class WalletAdminDTO {

    private Long userId;
    private String username;
    private Long walletId;
    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;
    private String tradeHistoryUrl; // e.g. /admin/wallets/{userId}/trades

    public WalletAdminDTO() {}

    public WalletAdminDTO(Long userId, String username, Long walletId,
                           BigDecimal availableBalance, BigDecimal lockedBalance) {
        this.userId = userId;
        this.username = username;
        this.walletId = walletId;
        this.availableBalance = availableBalance;
        this.lockedBalance = lockedBalance;
        this.tradeHistoryUrl = "/admin/wallets/" + userId + "/trades";
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Long getWalletId() { return walletId; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public String getTradeHistoryUrl() { return tradeHistoryUrl; }
}
