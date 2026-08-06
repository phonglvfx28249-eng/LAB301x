package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.dto.response.WalletResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.repository.WalletRepository;
import com.windle.blockchaintrading.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public boolean walletExistsForUser(Long userId) {
        return walletRepository.existsByUserId(userId);
    }

    @Transactional
    @Override
    public Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setAvailableBalance(BigDecimal.valueOf(100)); //
        wallet.setLockedBalance(BigDecimal.ZERO);

        return walletRepository.save(wallet);
    }

    @Override
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Override
    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id).orElse(null);
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public void deposit(Long walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        walletRepository.findByIdWithLock(walletId).ifPresent(wallet -> {
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
            walletRepository.save(wallet);
        });
    }

    @Override
    public void withdraw(Long walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient available balance for withdrawal");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    @Override
    public void lockFunds(Long walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to lock must be greater than zero");
        }

        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient available balance to lock");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setLockedBalance(wallet.getLockedBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void unlockFunds(Long walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to unlock must be greater than zero");
        }

        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (wallet.getLockedBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient locked balance to unlock");
        }

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    public void deleteWallet(Long id) {
        walletRepository.deleteById(id);
    }

    @Override
    public BigDecimal getAvailableBalance(Long userId) {
        return null;
    }

    @Override
    public WalletResponse getUserWalletResponse(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        // Get user's active FILLED orders to sum live unrealized PnL
        List<Order> activeOrders = orderRepository.findByUserIdAndStatus(userId, Order.OrderStatus.FILLED);

        BigDecimal totalUnrealizedPnl = activeOrders.stream()
                .map(Order::getUnrealizedPnl)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Returns WalletResponse where available_balance includes live PnL dynamically
        return WalletResponse.fromEntity(wallet, totalUnrealizedPnl);
    }
}