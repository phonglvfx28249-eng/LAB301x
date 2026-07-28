package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByBuyerIdOrSellerId(Long buyerId, Long sellerId, Pageable pageable);

    List<Trade> findByBuyOrderId(Long buyOrderId);

    List<Trade> findBySellOrderId(Long sellOrderId);

    @Query("""
        SELECT COALESCE(
            SUM(CASE WHEN t.buyer.id = :userId THEN t.quantity ELSE 0 END) 
            -   
            SUM(CASE WHEN t.seller.id = :userId THEN t.quantity ELSE 0 END), 
            0
        )
        FROM Trade t
        WHERE t.buyer.id = :userId OR t.seller.id = :userId
        """)
    BigDecimal calculateTotalCoinsOwnedByUserId(Long userId);

    @Query("""
        SELECT t FROM Trade t 
        WHERE t.buyer.id = :userId OR t.seller.id = :userId 
        ORDER BY t.createdAt DESC
    """)
    Page<Trade> findUserTradeHistory( Long userId, Pageable pageable);
}