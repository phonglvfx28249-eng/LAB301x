package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public TradeServiceImpl(TradeRepository tradeRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Trade executeTrade(Long buyOrderId, Long sellOrderId, Long buyerId, Long sellerId,
                              BigDecimal tradePrice, BigDecimal quantity) {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade quantity must be greater than zero");
        }
        if (tradePrice == null || tradePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade price must be greater than zero");
        }

        Order buyOrder = orderRepository.findById(buyOrderId)
                .orElseThrow(() -> new RuntimeException("Buy order not found with id: " + buyOrderId));
        Order sellOrder = orderRepository.findById(sellOrderId)
                .orElseThrow(() -> new RuntimeException("Sell order not found with id: " + sellOrderId));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + buyerId));
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        Trade trade = new Trade();
        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);
        trade.setBuyer(buyer);
        trade.setSeller(seller);
        trade.setTradePrice(tradePrice);
        trade.setQuantity(quantity);
        trade.setTotalAmount(tradePrice.multiply(quantity));

        // NOTE: this only creates the trade record. Updating each order's
        // remaining_quantity/status and posting the matching WalletTransaction
        // entries for both buyer and seller should happen alongside this call
        // (e.g. from an OrderMatchingService that orchestrates all three).

        return tradeRepository.save(trade);
    }

    @Override
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @Override
    public Trade getTradeById(Long id) {
        return tradeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Trade> getTradesByUserId(Long userId) {
        return tradeRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Override
    public List<Trade> getTradesByBuyOrderId(Long buyOrderId) {
        return tradeRepository.findByBuyOrderId(buyOrderId);
    }

    @Override
    public List<Trade> getTradesBySellOrderId(Long sellOrderId) {
        return tradeRepository.findBySellOrderId(sellOrderId);
    }
}