package com.windle.blockchaintrading.matching;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.service.TradeService;
import com.windle.blockchaintrading.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class MatchingEngineTest {

    @Mock
    private WalletService walletService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private TradeService tradeService;

    private MatchingEngine matchingEngine;

    private User buyer;
    private User seller;

    @BeforeEach
    void setUp() {
        matchingEngine = new MatchingEngine(walletService, orderRepository, tradeService);

        buyer = new User();
        buyer.setId(1L);

        seller = new User();
        seller.setId(2L);
    }

    @Test
    void matchOrder_shouldMatchBuyLimitOrderWithSellLimitOrder() {
        Order sellOrder = new Order();
        sellOrder.setId(101L);
        sellOrder.setUser(seller);
        sellOrder.setSide(Order.Side.SELL);
        sellOrder.setOrderType(Order.OrderType.LIMIT);
        sellOrder.setPrice(BigDecimal.valueOf(100));
        sellOrder.setQuantity(BigDecimal.valueOf(5));
        sellOrder.setRemainingQuantity(BigDecimal.valueOf(5));
        sellOrder.setStatus(Order.OrderStatus.PENDING);

        Order buyOrder = new Order();
        buyOrder.setId(102L);
        buyOrder.setUser(buyer);
        buyOrder.setSide(Order.Side.BUY);
        buyOrder.setOrderType(Order.OrderType.LIMIT);
        buyOrder.setPrice(BigDecimal.valueOf(105));
        buyOrder.setQuantity(BigDecimal.valueOf(5));
        buyOrder.setRemainingQuantity(BigDecimal.valueOf(5));
        buyOrder.setStatus(Order.OrderStatus.PENDING);

        when(orderRepository.findById(101L)).thenReturn(Optional.of(sellOrder));
        when(orderRepository.findById(102L)).thenReturn(Optional.of(buyOrder));

        // First add sell order to book
        matchingEngine.matchOrder(sellOrder);

        // Then match buy order against sell order
        boolean result = matchingEngine.matchOrder(buyOrder);

        assertTrue(result);
        verify(tradeService).executeTrade(
                eq(102L), eq(101L), eq(1L), eq(2L),
                eq(BigDecimal.valueOf(100)), eq(BigDecimal.valueOf(5))
        );
        assertEquals(BigDecimal.ZERO, buyOrder.getRemainingQuantity());
        assertEquals(BigDecimal.ZERO, sellOrder.getRemainingQuantity());
    }

    @Test
    void matchOrder_shouldPartialMatchWhenQuantitiesDiffer() {
        Order sellOrder = new Order();
        sellOrder.setId(201L);
        sellOrder.setUser(seller);
        sellOrder.setSide(Order.Side.SELL);
        sellOrder.setOrderType(Order.OrderType.LIMIT);
        sellOrder.setPrice(BigDecimal.valueOf(50));
        sellOrder.setQuantity(BigDecimal.valueOf(10));
        sellOrder.setRemainingQuantity(BigDecimal.valueOf(10));
        sellOrder.setStatus(Order.OrderStatus.PENDING);

        Order buyOrder = new Order();
        buyOrder.setId(202L);
        buyOrder.setUser(buyer);
        buyOrder.setSide(Order.Side.BUY);
        buyOrder.setOrderType(Order.OrderType.LIMIT);
        buyOrder.setPrice(BigDecimal.valueOf(50));
        buyOrder.setQuantity(BigDecimal.valueOf(4));
        buyOrder.setRemainingQuantity(BigDecimal.valueOf(4));
        buyOrder.setStatus(Order.OrderStatus.PENDING);

        when(orderRepository.findById(201L)).thenReturn(Optional.of(sellOrder));
        when(orderRepository.findById(202L)).thenReturn(Optional.of(buyOrder));

        matchingEngine.matchOrder(sellOrder);
        matchingEngine.matchOrder(buyOrder);

        verify(tradeService).executeTrade(
                eq(202L), eq(201L), eq(1L), eq(2L),
                eq(BigDecimal.valueOf(50)), eq(BigDecimal.valueOf(4))
        );
        assertEquals(BigDecimal.ZERO, buyOrder.getRemainingQuantity());
        assertEquals(BigDecimal.valueOf(6), sellOrder.getRemainingQuantity());
    }
}
