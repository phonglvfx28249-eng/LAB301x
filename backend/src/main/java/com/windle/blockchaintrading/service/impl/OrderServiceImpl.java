package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.dto.response.WalletResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.market.OMSLayer;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.service.OrderService;
import com.windle.blockchaintrading.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OMSLayer omsLayer;
    private final WalletService walletService;
    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OMSLayer omsLayer, WalletService walletService, TradeRepository tradeRepository, WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.omsLayer = omsLayer;
        this.walletService = walletService;
        this.tradeRepository = tradeRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
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

        if(omsLayer.validateBalance(userId, order)) {
            Wallet wallet = walletService.getWalletByUserId(userId);
            walletService.lockFunds(wallet.getId(), price.multiply(quantity)); // Lock the required funds for the order
            return orderRepository.save(order);
        } else {
            order.setStatus(Order.OrderStatus.REJECTED);
            orderRepository.save(order);
            throw new RuntimeException("Insufficient balance to place the order");
        }

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
                order.setStatus(Order.OrderStatus.PARTIAL);
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

    @Override
    public boolean checkOrderOwnByUser(Long orderId, Long userId) {
        return orderRepository.existsByIdAndUserId(orderId, userId);
    }

    @Override
    public List<Order> getOrdersByUserIdAndOrderStatus(Long userId, Order.OrderStatus orderStatus) {
        return orderRepository.findByUserIdAndStatus(userId, orderStatus);
    }


    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already closed.");
        }



        BigDecimal exitPrice = tradeRepository.findCurrentMarketPrice()
                .orElse(BigDecimal.valueOf(1.0));

        BigDecimal entryPrice = order.getPrice();
        BigDecimal quantity = order.getQuantity();

        // 1. Calculate Realized PnL
        BigDecimal finalPnL;
        if (order.getSide() == Order.Side.BUY) {
            finalPnL = exitPrice.subtract(entryPrice).multiply(quantity);
        } else {
            finalPnL = entryPrice.subtract(exitPrice).multiply(quantity);
        }

        // 2. Lock Order State
        if(order.getStatus() != Order.OrderStatus.FILLED) {
            // If the order was cancelled, we need to refund the locked funds
            finalPnL = BigDecimal.valueOf(0);
        }

        order.setRealizedPnl(finalPnL);
        order.setUnrealizedPnl(BigDecimal.ZERO);
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 3. Settle Wallet Base Cash ONCE
        Wallet wallet = walletRepository.findByUserIdWithLock(order.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        walletService.unlockFunds(wallet.getId(), wallet.getLockedBalance()); // Unlock the initial margin

        BigDecimal currentBalance = wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;


        wallet.setAvailableBalance(currentBalance.add(finalPnL));
        walletRepository.save(wallet);
    }


}