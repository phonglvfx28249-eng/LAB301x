package com.windle.blockchaintrading.market;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketDataServiceTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private MarketDataService marketDataService;

    @Test
    void getMaxPrice24h_shouldReturnMaxPriceOrZero() {
        when(tradeRepository.findMaxPriceInWindow(any(LocalDateTime.class)))
                .thenReturn(Optional.of(BigDecimal.valueOf(250)));

        BigDecimal maxPrice = marketDataService.getMaxPrice24h();

        assertEquals(BigDecimal.valueOf(250), maxPrice);
    }

    @Test
    void getMinPrice24h_shouldReturnMinPriceOrZero() {
        when(tradeRepository.findMinPriceInWindow(any(LocalDateTime.class)))
                .thenReturn(Optional.of(BigDecimal.valueOf(100)));

        BigDecimal minPrice = marketDataService.getMinPrice24h();

        assertEquals(BigDecimal.valueOf(100), minPrice);
    }

    @Test
    void getVolume24h_shouldReturnCount() {
        when(tradeRepository.countByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(42L);

        long volume = marketDataService.getVolume24h();

        assertEquals(42L, volume);
    }

    @Test
    void getTop10BuyOrders_shouldCallOrderRepository() {
        Order o1 = new Order();
        o1.setSide(Order.Side.BUY);
        when(orderRepository.findTop10BySideAndStatusInOrderByCreatedAtDesc(eq(Order.Side.BUY), any()))
                .thenReturn(List.of(o1));

        List<Order> result = marketDataService.getTop10BuyOrders();

        assertEquals(1, result.size());
        assertEquals(Order.Side.BUY, result.get(0).getSide());
    }

    @Test
    void getTop5Trades_shouldReturnOrdersFromTrades() {
        Order buyOrder = new Order();
        buyOrder.setId(1L);
        Order sellOrder = new Order();
        sellOrder.setId(2L);

        Trade trade = new Trade();
        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);

        when(tradeRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(trade));

        List<Order> result = marketDataService.getTop5Trades();

        assertEquals(2, result.size());
        assertTrue(result.contains(buyOrder));
        assertTrue(result.contains(sellOrder));
    }
}
