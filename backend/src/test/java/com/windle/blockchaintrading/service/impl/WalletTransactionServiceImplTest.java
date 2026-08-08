package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.entity.WalletTransaction;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.repository.WalletTransactionRepository;
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
class WalletTransactionServiceImplTest {

    @Mock
    private WalletTransactionRepository walletTransactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private WalletTransactionServiceImpl walletTransactionService;

    private Wallet sampleWallet;

    @BeforeEach
    void setUp() {
        sampleWallet = new Wallet();
        sampleWallet.setId(10L);
    }

    @Test
    void recordTransaction_shouldCreateAndSaveTransaction() {
        when(walletRepository.findById(10L)).thenReturn(Optional.of(sampleWallet));
        when(walletTransactionRepository.save(any(WalletTransaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        WalletTransaction wt = walletTransactionService.recordTransaction(
                10L, null, null,
                WalletTransaction.TransactionType.DEPOSIT,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(500), BigDecimal.valueOf(600),
                BigDecimal.ZERO, BigDecimal.ZERO,
                "Test Deposit"
        );

        assertNotNull(wt);
        assertEquals(sampleWallet, wt.getWallet());
        assertEquals(WalletTransaction.TransactionType.DEPOSIT, wt.getTransactionType());
        assertEquals("Test Deposit", wt.getDescription());
    }

    @Test
    void getTransactionsByWalletId_shouldCallRepository() {
        WalletTransaction wt = new WalletTransaction();
        when(walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(wt));

        List<WalletTransaction> result = walletTransactionService.getTransactionsByWalletId(10L);

        assertEquals(1, result.size());
    }
}
