package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order placeOrder(Long userId, Order.Side side, Order.OrderType orderType, BigDecimal price, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setSide(side);
        order.setOrderType(orderType);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setRemainingQuantity(quantity);
        order.setStatus(Order.OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void updateOrderStatus(Long id, Order.OrderStatus status) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    @Override
    public void updateRemainingQuantity(Long id, BigDecimal remainingQuantity) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setRemainingQuantity(remainingQuantity);

            // Auto-flip status based on how much of the order is left to fill
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                order.setStatus(Order.OrderStatus.FILLED);
            } else if (remainingQuantity.compareTo(order.getQuantity()) < 0) {
                order.setStatus(Order.OrderStatus.PARTIALLY_FILLED);
            }

            orderRepository.save(order);
        });
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            if (order.getStatus() == Order.OrderStatus.FILLED) {
                throw new IllegalStateException("Cannot cancel an order that is already filled");
            }
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}