package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.entity.WalletTransaction;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.repository.WalletTransactionRepository;
import com.windle.blockchaintrading.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    @Autowired
    public WalletTransactionServiceImpl(WalletTransactionRepository walletTransactionRepository,
                                        WalletRepository walletRepository,
                                        OrderRepository orderRepository,
                                        TradeRepository tradeRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public WalletTransaction recordTransaction(Long walletId,
                                               Long orderId,
                                               Long tradeId,
                                               WalletTransaction.TransactionType transactionType,
                                               BigDecimal amount,
                                               BigDecimal availableBefore,
                                               BigDecimal availableAfter,
                                               BigDecimal lockedBefore,
                                               BigDecimal lockedAfter,
                                               String description) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);

        // order_id and trade_id are both optional - only one, either, or neither may be set
        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            transaction.setOrder(order);
        }

        if (tradeId != null) {
            Trade trade = tradeRepository.findById(tradeId)
                    .orElseThrow(() -> new RuntimeException("Trade not found with id: " + tradeId));
            transaction.setTrade(trade);
        }

        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setAvailableBefore(availableBefore);
        transaction.setAvailableAfter(availableAfter);
        transaction.setLockedBefore(lockedBefore);
        transaction.setLockedAfter(lockedAfter);
        transaction.setDescription(description);

        return walletTransactionRepository.save(transaction);
    }

    @Override
    public List<WalletTransaction> getAllTransactions() {
        return walletTransactionRepository.findAll();
    }

    @Override
    public WalletTransaction getTransactionById(Long id) {
        return walletTransactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<WalletTransaction> getTransactionsByWalletId(Long walletId) {
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    @Override
    public List<WalletTransaction> getTransactionsByOrderId(Long orderId) {
        return walletTransactionRepository.findByOrderId(orderId);
    }

    @Override
    public List<WalletTransaction> getTransactionsByTradeId(Long tradeId) {
        return walletTransactionRepository.findByTradeId(tradeId);
    }
}