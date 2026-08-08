package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.market.OMSLayer;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.service.WalletService;
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

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OMSLayer omsLayer;
    @Mock
    private WalletService walletService;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User sampleUser;
    private Wallet sampleWallet;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);

        sampleWallet = new Wallet();
        sampleWallet.setId(10L);
        sampleWallet.setUser(sampleUser);
        sampleWallet.setAvailableBalance(BigDecimal.valueOf(1000));
        sampleWallet.setLockedBalance(BigDecimal.valueOf(100));

        sampleOrder = new Order();
        sampleOrder.setId(100L);
        sampleOrder.setUser(sampleUser);
        sampleOrder.setSide(Order.Side.BUY);
        sampleOrder.setOrderType(Order.OrderType.LIMIT);
        sampleOrder.setPrice(BigDecimal.valueOf(50));
        sampleOrder.setQuantity(BigDecimal.valueOf(2));
        sampleOrder.setRemainingQuantity(BigDecimal.valueOf(2));
        sampleOrder.setStatus(Order.OrderStatus.PENDING);
    }

    @Test
    void placeOrder_shouldThrowExceptionWhenQuantityZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(1L, Order.Side.BUY, Order.OrderType.LIMIT, BigDecimal.valueOf(50), BigDecimal.ZERO)
        );
    }

    @Test
    void placeOrder_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                orderService.placeOrder(99L, Order.Side.BUY, Order.OrderType.LIMIT, BigDecimal.valueOf(50), BigDecimal.valueOf(1))
        );
    }

    @Test
    void placeOrder_shouldSaveAndReturnOrderWhenBalanceValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(omsLayer.validateBalance(eq(1L), any(Order.class))).thenReturn(true);
        when(walletService.getWalletByUserId(1L)).thenReturn(sampleWallet);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(1L, Order.Side.BUY, Order.OrderType.LIMIT, BigDecimal.valueOf(50), BigDecimal.valueOf(2));

        assertNotNull(result);
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        verify(walletService).lockFunds(10L, BigDecimal.valueOf(100));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_shouldRejectOrderWhenInsufficientBalance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(omsLayer.validateBalance(eq(1L), any(Order.class))).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                orderService.placeOrder(1L, Order.Side.BUY, Order.OrderType.LIMIT, BigDecimal.valueOf(50), BigDecimal.valueOf(2))
        );

        verify(orderRepository).save(argThat(o -> o.getStatus() == Order.OrderStatus.REJECTED));
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

        Order result = orderService.getOrderById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void updateRemainingQuantity_shouldFlipStatusToFilledWhenZero() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

        orderService.updateRemainingQuantity(100L, BigDecimal.ZERO);

        assertEquals(Order.OrderStatus.FILLED, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void updateRemainingQuantity_shouldFlipStatusToPartialWhenReduced() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

        orderService.updateRemainingQuantity(100L, BigDecimal.valueOf(1));

        assertEquals(Order.OrderStatus.PARTIAL, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void cancelOrder_shouldThrowExceptionIfAlreadyFilled() {
        sampleOrder.setStatus(Order.OrderStatus.FILLED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(100L));
    }

    @Test
    void cancelOrder_shouldSetStatusToCancelled() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

        orderService.cancelOrder(100L);

        assertEquals(Order.OrderStatus.CANCELLED, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void closeOrder_shouldCalculatePnLAndSettleWallet() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));
        when(tradeRepository.findCurrentMarketPrice()).thenReturn(Optional.of(BigDecimal.valueOf(60)));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sampleWallet));

        orderService.closeOrder(100L);

        assertEquals(Order.OrderStatus.CANCELLED, sampleOrder.getStatus());
        verify(walletService).unlockFunds(10L, sampleWallet.getLockedBalance());
        verify(walletRepository).save(sampleWallet);
    }
}
