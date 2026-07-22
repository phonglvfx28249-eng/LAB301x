package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletTransactionService {

    WalletTransaction recordTransaction(Long walletId,
                                        Long orderId,
                                        Long tradeId,
                                        WalletTransaction.TransactionType transactionType,
                                        BigDecimal amount,
                                        BigDecimal availableBefore,
                                        BigDecimal availableAfter,
                                        BigDecimal lockedBefore,
                                        BigDecimal lockedAfter,
                                        String description);

    List<WalletTransaction> getAllTransactions();

    WalletTransaction getTransactionById(Long id);

    List<WalletTransaction> getTransactionsByWalletId(Long walletId);

    List<WalletTransaction> getTransactionsByOrderId(Long orderId);

    List<WalletTransaction> getTransactionsByTradeId(Long tradeId);
}