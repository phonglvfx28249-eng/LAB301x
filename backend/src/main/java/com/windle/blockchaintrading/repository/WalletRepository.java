package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUser(User user);


    Optional<Wallet> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithLock(@Param("userId") Long userId);

    @Query("SELECT w.availableBalance FROM Wallet w WHERE w.user.id = :userId")
    Optional<BigDecimal> findAvailableBalanceByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT u.id, u.username, w.id, w.availableBalance, w.lockedBalance
        FROM Wallet w
        JOIN User u ON u.id = w.user.id
        WHERE (:search IS NULL OR :search = ''
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Object[]> searchWithUsername(@Param("search") String search, Pageable pageable);

}