package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    boolean walletExistsForUser(Long userId);

    Wallet createWalletForUser(Long userId);

    List<Wallet> getAllWallets();

    Wallet getWalletById(Long id);

    Wallet getWalletByUserId(Long userId);

    void deposit(Long walletId, BigDecimal amount);

    void withdraw(Long walletId, BigDecimal amount);

    void lockFunds(Long walletId, BigDecimal amount);

    void unlockFunds(Long walletId, BigDecimal amount);

    void deleteWallet(Long id);
}