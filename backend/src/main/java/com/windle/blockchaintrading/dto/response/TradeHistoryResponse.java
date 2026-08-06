package com.windle.blockchaintrading.dto.response;

import com.windle.blockchaintrading.entity.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record TradeHistoryResponse(
        String id,
        String symbol,
        String coin,
        String status,
        String statusColor,
        String position,
        String positionColor,
        BigDecimal amount,
        String time,
        BigDecimal price,
        String roi,
        String earned
) {
    public static TradeHistoryResponse fromOrder(Order order, BigDecimal currentMarketPrice) {
        boolean isBuy = order.getSide() == Order.Side.BUY;
        BigDecimal entryPrice = order.getPrice() != null ? order.getPrice() : BigDecimal.ZERO;
        BigDecimal quantity = order.getQuantity() != null ? order.getQuantity() : BigDecimal.ZERO;

        // Calculate PnL
        BigDecimal pnl = BigDecimal.ZERO;
        if (currentMarketPrice != null && entryPrice.compareTo(BigDecimal.ZERO) > 0) {
            pnl = isBuy
                    ? currentMarketPrice.subtract(entryPrice).multiply(quantity)
                    : entryPrice.subtract(currentMarketPrice).multiply(quantity);
        }

        // Calculate ROI % = (PnL / Initial Margin) * 100
        BigDecimal initialMargin = entryPrice.multiply(quantity);
        BigDecimal roiValue = initialMargin.compareTo(BigDecimal.ZERO) > 0
                ? pnl.divide(initialMargin, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        boolean isPositive = pnl.compareTo(BigDecimal.ZERO) >= 0;

        return new TradeHistoryResponse(
                "wid-" + order.getId(),
                "W",
                "WID",
                order.getStatus().name(),
                getStatusColor(order.getStatus()),
                isBuy ? "Long" : "Short",
                isBuy ? "text-green-600" : "text-red-500",
                quantity,
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : "",
                entryPrice,
                (isPositive ? "+" : "") + roiValue.setScale(2, RoundingMode.HALF_UP) + "%",
                (isPositive ? "+" : "") + pnl.setScale(2, RoundingMode.HALF_UP) + " $USD"
        );
    }

    private static String getStatusColor(Order.OrderStatus status) {
        return switch (status) {
            case FILLED -> "bg-green-500";
            case PARTIAL -> "bg-yellow-500";
            case OPEN, PENDING -> "bg-gray-300";
            case CANCELLED, REJECTED-> "bg-red-500";
        };
    }
}