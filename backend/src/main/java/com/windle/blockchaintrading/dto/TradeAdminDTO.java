package com.windle.blockchaintrading.dto;


import com.windle.blockchaintrading.entity.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeAdminDTO {

    private Long id;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal tradePrice;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public static TradeAdminDTO from(Trade t) {
        TradeAdminDTO dto = new TradeAdminDTO();
        dto.id = t.getId();
        dto.buyerId = t.getBuyer().getId();
        dto.sellerId = t.getSeller().getId();
        dto.tradePrice = t.getTradePrice();
        dto.quantity = t.getQuantity();
        dto.totalAmount = t.getTotalAmount();
        dto.createdAt = t.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getBuyerId() { return buyerId; }
    public Long getSellerId() { return sellerId; }
    public BigDecimal getTradePrice() { return tradePrice; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
