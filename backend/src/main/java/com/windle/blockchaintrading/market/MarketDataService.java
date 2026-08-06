package com.windle.blockchaintrading.market;


import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketDataService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public MarketDataService(TradeRepository tradeRepository, OrderRepository orderRepository) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
    }

    public BigDecimal getMaxPrice24h(){
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now(ZoneOffset.UTC).minusHours(24);
        return tradeRepository.findMaxPriceInWindow(twentyFourHoursAgo).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMinPrice24h(){
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now(ZoneOffset.UTC).minusHours(24);
        return tradeRepository.findMinPriceInWindow(twentyFourHoursAgo).orElse(BigDecimal.ZERO);
    }

    public long getVolume24h(){
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now(ZoneOffset.UTC).minusHours(24);
        return tradeRepository.countByCreatedAtGreaterThanEqual(twentyFourHoursAgo);
    }

    public List<Order> getTop10BuyOrders() {
        List<Order.OrderStatus> targetStatuses = List.of(
                Order.OrderStatus.PENDING,
                Order.OrderStatus.OPEN,
                Order.OrderStatus.PARTIAL
        );

        return orderRepository.findTop10BySideAndStatusInOrderByCreatedAtDesc(
                Order.Side.BUY,
                targetStatuses
        );
    }

    public List<Order> getTop10SellOrders() {
        List<Order.OrderStatus> targetStatuses = List.of(
                Order.OrderStatus.PENDING,
                Order.OrderStatus.OPEN,
                Order.OrderStatus.PARTIAL
        );

        return orderRepository.findTop10BySideAndStatusInOrderByCreatedAtDesc(
                Order.Side.SELL,
                targetStatuses
        );
    }

    public List<Order> getTop5Trades() {
        List<Trade> top5Trades = tradeRepository.findTop5ByOrderByCreatedAtDesc();
        List<Order> orders = new ArrayList<>();
        for (Trade trade : top5Trades) {
            orders.add(trade.getBuyOrder());
            orders.add(trade.getSellOrder());
        }
        return orders;
    }








}
