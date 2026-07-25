package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(Long id,
                            String side,
                            String order_type,
                            BigDecimal price,
                            BigDecimal quantity,
                            BigDecimal remaining_quantity,
                            String status,
                            LocalDateTime create_at,
                            LocalDateTime update_at) {

    //  Convenience constructor mapping from the Order entity directly
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSide().name(),
                order.getOrderType().name(),
                order.getPrice(),
                order.getQuantity(),
                order.getRemainingQuantity(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
