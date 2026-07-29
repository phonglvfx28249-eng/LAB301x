package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.dto.response.CandleStickResponse;
import com.windle.blockchaintrading.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Query("SELECT MAX(t.tradePrice) FROM Trade t " +
            "WHERE t.createdAt >= :since")
    Optional<BigDecimal> findMaxPriceInWindow(
            @Param("since") LocalDateTime since
    );

    @Query("SELECT MIN(t.tradePrice) FROM Trade t " +
                  "WHERE t.createdAt >= :since")
    Optional<BigDecimal> findMinPriceInWindow(@Param("since") LocalDateTime since);

    long countByCreatedAtGreaterThanEqual(LocalDateTime since);

    List<Trade> findTop5ByOrderByCreatedAtDesc();

    @Query(value = """
        SELECT 
            FROM_UNIXTIME(UNIX_TIMESTAMP(created_at) - (UNIX_TIMESTAMP(created_at) % :seconds)) AS time,
            CAST(SUBSTRING_INDEX(GROUP_CONCAT(price ORDER BY created_at ASC, id ASC), ',', 1) AS DECIMAL(18,8)) AS openPrice,
            MAX(price) AS highPrice,
            MIN(price) AS lowPrice,
            CAST(SUBSTRING_INDEX(GROUP_CONCAT(price ORDER BY created_at DESC, id DESC), ',', 1) AS DECIMAL(18,8)) AS closePrice
        FROM trades
        GROUP BY time
        ORDER BY time ASC
        """, nativeQuery = true)
    List<CandleStickResponse> findCandlesticksByInterval(
            @Param("seconds") int seconds
    );

}