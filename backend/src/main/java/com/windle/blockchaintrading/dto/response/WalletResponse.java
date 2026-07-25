package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(Long id,
                             BigDecimal available_balance,
                             BigDecimal locked_balance,
                             LocalDateTime create_at,
                             LocalDateTime update_at) {
    // Convenience constructor mapping from the Wallet entity directly
    public static WalletResponse fromEntity(Wallet wallet){
        return new WalletResponse(
                wallet.getId(),
                wallet.getAvailableBalance(),
                wallet.getLockedBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}