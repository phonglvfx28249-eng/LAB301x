package com.windle.blockchaintrading.dto.response;

import java.math.BigDecimal;

public record OrderProfitResponse(
        Long orderId,
        BigDecimal entryPrice,
        BigDecimal currentMarketPrice,
        BigDecimal quantity,
        String side,
        String status,
        BigDecimal pnl // Positive or negative profit
) {}
