package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

    List<Order> findBySideAndStatus(Order.Side side, Order.OrderStatus status);

    List<Order> findBySideAndOrderTypeAndStatus(Order.Side side, Order.OrderType orderType, Order.OrderStatus status);
}