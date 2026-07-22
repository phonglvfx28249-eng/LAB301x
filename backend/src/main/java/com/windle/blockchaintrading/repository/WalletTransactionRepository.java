package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletId(Long walletId);

    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<WalletTransaction> findByOrderId(Long orderId);

    List<WalletTransaction> findByTradeId(Long tradeId);

    List<WalletTransaction> findByTransactionType(WalletTransaction.TransactionType transactionType);
}