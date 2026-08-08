package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.dto.response.WalletResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User sampleUser;
    private Wallet sampleWallet;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);

        sampleWallet = new Wallet();
        sampleWallet.setId(10L);
        sampleWallet.setUser(sampleUser);
        sampleWallet.setAvailableBalance(BigDecimal.valueOf(500));
        sampleWallet.setLockedBalance(BigDecimal.valueOf(100));
    }

    @Test
    void createWalletForUser_shouldInitializeDefaultBalance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = walletService.createWalletForUser(1L);

        assertNotNull(wallet);
        assertEquals(BigDecimal.valueOf(100), wallet.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, wallet.getLockedBalance());
        assertEquals(sampleUser, wallet.getUser());
    }

    @Test
    void deposit_shouldAddAmountToAvailableBalance() {
        when(walletRepository.findByIdWithLock(10L)).thenReturn(Optional.of(sampleWallet));

        walletService.deposit(10L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(700), sampleWallet.getAvailableBalance());
        verify(walletRepository).save(sampleWallet);
    }

    @Test
    void withdraw_shouldSubtractAmountFromAvailableBalance() {
        when(walletRepository.findByIdWithLock(10L)).thenReturn(Optional.of(sampleWallet));

        walletService.withdraw(10L, BigDecimal.valueOf(300));

        assertEquals(BigDecimal.valueOf(200), sampleWallet.getAvailableBalance());
        verify(walletRepository).save(sampleWallet);
    }

    @Test
    void withdraw_shouldThrowExceptionWhenInsufficientBalance() {
        when(walletRepository.findByIdWithLock(10L)).thenReturn(Optional.of(sampleWallet));

        assertThrows(IllegalStateException.class, () ->
                walletService.withdraw(10L, BigDecimal.valueOf(1000))
        );
    }

    @Test
    void lockFunds_shouldMoveAmountFromAvailableToLocked() {
        when(walletRepository.findByIdWithLock(10L)).thenReturn(Optional.of(sampleWallet));

        walletService.lockFunds(10L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(300), sampleWallet.getAvailableBalance());
        assertEquals(BigDecimal.valueOf(300), sampleWallet.getLockedBalance());
        verify(walletRepository).save(sampleWallet);
    }

    @Test
    void unlockFunds_shouldMoveAmountFromLockedToAvailable() {
        when(walletRepository.findByIdWithLock(10L)).thenReturn(Optional.of(sampleWallet));

        walletService.unlockFunds(10L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(550), sampleWallet.getAvailableBalance());
        assertEquals(BigDecimal.valueOf(50), sampleWallet.getLockedBalance());
        verify(walletRepository).save(sampleWallet);
    }

    @Test
    void getUserWalletResponse_shouldCalculateUnrealizedPnlFromActiveOrders() {
        Order filledOrder = new Order();
        filledOrder.setStatus(Order.OrderStatus.FILLED);
        filledOrder.setUnrealizedPnl(BigDecimal.valueOf(50));

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(sampleWallet));
        when(orderRepository.findByUserIdAndStatus(1L, Order.OrderStatus.FILLED)).thenReturn(List.of(filledOrder));

        WalletResponse response = walletService.getUserWalletResponse(1L);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(BigDecimal.valueOf(550), response.available_balance());
    }
}
