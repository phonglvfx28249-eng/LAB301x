package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.dto.response.UserTradeResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public TradeServiceImpl(TradeRepository tradeRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
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

        return tradeRepository.save(trade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Trade getTradeById(Long id) {
        return tradeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Trade> getTradesByUserId(Long userId, Pageable pageable) {
        return tradeRepository.findByBuyerIdOrSellerId(userId, userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> getTradesByBuyOrderId(Long buyOrderId) {
        return tradeRepository.findByBuyOrderId(buyOrderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> getTradesBySellOrderId(Long sellOrderId) {
        return tradeRepository.findBySellOrderId(sellOrderId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalCoinsOwnedByUserId(Long userId) {
        return tradeRepository.calculateTotalCoinsOwnedByUserId(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserTradeResponse> getUserTradesPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trade> tradePage = tradeRepository.findUserTradeHistory(userId, pageable);

        return tradePage.map(trade -> {
            // Determine side based on target userId context
            String side = trade.getBuyer().getId().equals(userId) ? "BUY" : "SELL";

            return new UserTradeResponse(
                    trade.getId(),
                    trade.getTradePrice(),
                    trade.getQuantity(),
                    trade.getTotalAmount(),
                    side,
                    trade.getCreatedAt()
            );
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserTradeResponse getTradeByIdAndUserId(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + tradeId));

        // Security Check: Verify that the authenticated user is either the buyer or the seller
        boolean isBuyer = trade.getBuyer().getId().equals(userId);
        boolean isSeller = trade.getSeller().getId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new SecurityException("Unauthorized access to trade resource");
        }

        // Determine side relative to the requesting user
        String side = isBuyer ? "BUY" : "SELL";

        return new UserTradeResponse(
                trade.getId(),
                trade.getTradePrice(),
                trade.getQuantity(),
                trade.getTotalAmount(),
                side,
                trade.getCreatedAt()
        );
    }
}