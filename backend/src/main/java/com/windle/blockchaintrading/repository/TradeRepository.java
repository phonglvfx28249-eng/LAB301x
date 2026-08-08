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
            bucket.time_bucket AS time,
            MIN(bucket.open_price) AS openPrice,
            MAX(bucket.trade_price) AS highPrice,
            MIN(bucket.trade_price) AS lowPrice,
            MAX(bucket.close_price) AS closePrice
        FROM (
            SELECT 
                trade_price,
                FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(created_at) / :seconds) * :seconds) AS time_bucket,
                FIRST_VALUE(trade_price) OVER (
                    PARTITION BY FLOOR(UNIX_TIMESTAMP(created_at) / :seconds) 
                    ORDER BY created_at ASC, id ASC
                ) AS open_price,
                FIRST_VALUE(trade_price) OVER (
                    PARTITION BY FLOOR(UNIX_TIMESTAMP(created_at) / :seconds) 
                    ORDER BY created_at DESC, id DESC
                ) AS close_price
            FROM trades
        ) AS bucket
        GROUP BY bucket.time_bucket
        ORDER BY bucket.time_bucket ASC
        """, nativeQuery = true)
    List<CandleStickResponse> findCandlesticksByInterval(
            @Param("seconds") int seconds
    );

    @Query("SELECT t.tradePrice FROM Trade t ORDER BY t.id DESC LIMIT 1")
    Optional<BigDecimal> findCurrentMarketPrice();

    Page<Trade> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Trade> findByBuyerIdOrSellerId(Long buyerId, Long sellerId, Pageable pageable);

}