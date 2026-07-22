package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);

    List<Trade> findByBuyOrderId(Long buyOrderId);

    List<Trade> findBySellOrderId(Long sellOrderId);
}