package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserTradeResponse(
        Long id,
        BigDecimal trade_price,
        BigDecimal quantity,
        BigDecimal total_amount,
        LocalDateTime create_at
) {
    // Convenience constructor mapping from the Trade entity directly
    public static UserTradeResponse fromEntity(Trade trade) {
        return new UserTradeResponse(
                trade.getId(),
                trade.getTradePrice(),
                trade.getQuantity(),
                trade.getTotalAmount(),
                trade.getCreatedAt()
        );
    }

}