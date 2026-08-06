package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(
        Long id,
        BigDecimal available_balance, // Real cash + Live floating PnL
        BigDecimal locked_balance,
        LocalDateTime create_at,
        LocalDateTime update_at
) {
    public static WalletResponse fromEntity(Wallet wallet, BigDecimal totalUnrealizedPnl) {
        BigDecimal realCash = wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;
        BigDecimal pnl = totalUnrealizedPnl != null ? totalUnrealizedPnl : BigDecimal.ZERO;

        // Combines real cash + live paper PnL dynamically in-memory
        BigDecimal totalAvailable = realCash.add(pnl);

        return new WalletResponse(
                wallet.getId(),
                totalAvailable,
                wallet.getLockedBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}