package com.windle.blockchaintrading.dto.request;

import java.math.BigDecimal;

public record OrderRequest(
        Long id,
        Long userId,
        String side,
        String type,
        BigDecimal price,
        BigDecimal quantity) {
}