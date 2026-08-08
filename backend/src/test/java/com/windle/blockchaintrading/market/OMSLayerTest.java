package com.windle.blockchaintrading.market;

import com.windle.blockchaintrading.dto.response.OrderProfitResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.matching.MatchingEngine;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class OMSLayerTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private MatchingEngine matchingEngine;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OMSLayer omsLayer;

    private Wallet sampleWallet;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleWallet = new Wallet();
        sampleWallet.setId(1L);
        sampleWallet.setAvailableBalance(BigDecimal.valueOf(1000));

        sampleOrder = new Order();
        sampleOrder.setId(10L);
        sampleOrder.setSide(Order.Side.BUY);
        sampleOrder.setOrderType(Order.OrderType.LIMIT);
        sampleOrder.setPrice(BigDecimal.valueOf(100));
        sampleOrder.setQuantity(BigDecimal.valueOf(2));
        sampleOrder.setStatus(Order.OrderStatus.FILLED);
    }

    @Test
    void validateBalance_shouldReturnTrueWhenBalanceSufficient() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sampleWallet));

        boolean isValid = omsLayer.validateBalance(1L, sampleOrder);

        assertTrue(isValid);
    }

    @Test
    void validateBalance_shouldReturnFalseWhenBalanceInsufficient() {
        sampleWallet.setAvailableBalance(BigDecimal.valueOf(50));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sampleWallet));

        boolean isValid = omsLayer.validateBalance(1L, sampleOrder);

        assertFalse(isValid);
    }

    @Test
    void matchingEngineOrder_shouldProcessPendingAndOpenOrders() {
        Order pendingOrder = new Order();
        pendingOrder.setId(100L);
        pendingOrder.setStatus(Order.OrderStatus.PENDING);

        when(orderRepository.findAll()).thenReturn(List.of(pendingOrder));

        omsLayer.matchingEngineOrder();

        verify(matchingEngine).matchOrder(pendingOrder);
    }

    @Test
    void calculateProfit_shouldUpdateFloatingPnLForFilledBuyOrders() {
        when(tradeRepository.findCurrentMarketPrice()).thenReturn(Optional.of(BigDecimal.valueOf(120)));
        when(orderRepository.findByStatus(Order.OrderStatus.FILLED)).thenReturn(List.of(sampleOrder));

        omsLayer.calculateProfit();

        // PnL for BUY: (120 - 100) * 2 = 40
        assertEquals(BigDecimal.valueOf(40), sampleOrder.getUnrealizedPnl());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void calculateSingleOrderProfit_shouldCalculateProfitForFilledOrder() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(tradeRepository.findCurrentMarketPrice()).thenReturn(Optional.of(BigDecimal.valueOf(150)));

        OrderProfitResponse response = omsLayer.calculateSingleOrderProfit(10L);

        assertNotNull(response);
        // (150 - 100) * 2 = 100 PnL
        assertEquals(BigDecimal.valueOf(100), response.pnl());
    }
}
