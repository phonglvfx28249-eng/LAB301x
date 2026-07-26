package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.Trade;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface TradeService {

    Trade executeTrade(Long buyOrderId, Long sellOrderId, Long buyerId, Long sellerId,
                       BigDecimal tradePrice, BigDecimal quantity);

    List<Trade> getAllTrades();

    Trade getTradeById(Long id);

    List<Trade> getTradesByUserId(Long userId, Pageable pageable);

    List<Trade> getTradesByBuyOrderId(Long buyOrderId);

    List<Trade> getTradesBySellOrderId(Long sellOrderId);
}