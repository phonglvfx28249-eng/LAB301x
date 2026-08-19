package com.windle.blockchaintrading.market;

import com.windle.blockchaintrading.dto.response.OrderProfitResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.matching.MatchingEngine;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.service.OrderService;
import com.windle.blockchaintrading.service.WalletService;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OMSLayer {

    private final WalletRepository walletRepository;
    private final MatchingEngine matchingEngine;
    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OMSLayer(WalletRepository walletRepository, MatchingEngine matchingEngine, TradeRepository tradeRepository, OrderRepository orderRepository) {
        this.walletRepository = walletRepository;
        this.matchingEngine = matchingEngine;
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;

    }


    @Transactional
    public boolean validateBalance(Long userId, Order order) {

        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user with id: " + userId));

       //Future trading so just check if the user has enough available balance to cover the order's total cost (price * quantity)
        BigDecimal requiredAmount = order.getQuantity().multiply(order.getPrice());
        return wallet.getAvailableBalance().compareTo(requiredAmount) >= 0;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void matchingEngineOrder(){
        List<Order> pendingOrders = orderRepository.findAll();

        // sorted order by id
        pendingOrders.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

        for (Order order : pendingOrders) {
            if (order.getStatus() == Order.OrderStatus.PENDING ||
                    order.getStatus() == Order.OrderStatus.OPEN ||
                    order.getStatus() == Order.OrderStatus.PARTIAL) {
                matchingEngine.matchOrder(order);
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void calculateProfit() {
        BigDecimal currentMarketPrice = tradeRepository.findCurrentMarketPrice().orElse(null);

        if (currentMarketPrice == null || currentMarketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        List<Order> activeOrders = orderRepository.findByStatus(Order.OrderStatus.FILLED);

        for (Order order : activeOrders) {
            if (order.getPrice() == null || order.getQuantity() == null) {
                continue;
            }

            BigDecimal entryPrice = order.getPrice();
            BigDecimal quantity = order.getQuantity();
            BigDecimal pnl;

            if (order.getSide() == Order.Side.BUY) {
                pnl = currentMarketPrice.subtract(entryPrice).multiply(quantity);
            } else {
                pnl = entryPrice.subtract(currentMarketPrice).multiply(quantity);
            }

            // Update floating PnL on order object
            order.setUnrealizedPnl(pnl);
            orderRepository.save(order);
        }
    }

    @Transactional(readOnly = true)
    public OrderProfitResponse calculateSingleOrderProfit(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        BigDecimal currentMarketPrice = tradeRepository.findCurrentMarketPrice()
                .orElse(order.getPrice()); // Fallback to entry price if no market trades exist yet

        BigDecimal entryPrice = order.getPrice();
        BigDecimal quantity = order.getQuantity();
        BigDecimal pnl;

        if (order.getSide() == Order.Side.BUY) {
            // LONG: (Market Price - Entry Price) * Quantity
            pnl = currentMarketPrice.subtract(entryPrice).multiply(quantity);
        } else {
            // SHORT: (Entry Price - Market Price) * Quantity
            pnl = entryPrice.subtract(currentMarketPrice).multiply(quantity);
        }

        if(order.getStatus() != Order.OrderStatus.FILLED){
            pnl = BigDecimal.ZERO; // No profit/loss for non-filled orders
        }

        return new OrderProfitResponse(
                order.getId(),
                entryPrice,
                currentMarketPrice,
                quantity,
                order.getSide().name(),
                order.getStatus().name(),
                pnl
        );
    }

}

