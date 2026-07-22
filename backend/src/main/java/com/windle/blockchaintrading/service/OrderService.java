package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Order placeOrder(Long userId, Order.Side side, Order.OrderType orderType, BigDecimal price, BigDecimal quantity);

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> getOrdersByStatus(Order.OrderStatus status);

    void updateOrderStatus(Long id, Order.OrderStatus status);

    void updateRemainingQuantity(Long id, BigDecimal remainingQuantity);

    void cancelOrder(Long id);

    void deleteOrder(Long id);
}