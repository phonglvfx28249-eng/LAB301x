package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

    List<Order> findBySideAndStatus(Order.Side side, Order.OrderStatus status);

    List<Order> findBySideAndOrderTypeAndStatus(Order.Side side, Order.OrderType orderType, Order.OrderStatus status);

    List<Order> findTop10BySideAndStatusInOrderByCreatedAtDesc(
            Order.Side side,
            List<Order.OrderStatus> statuses
    );

    boolean existsByIdAndUserId(Long id, Long userId);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}