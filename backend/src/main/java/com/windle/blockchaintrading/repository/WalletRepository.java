package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUser(User user);

    // Convenience finder for the common case of "one wallet per user"
    Optional<Wallet> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}