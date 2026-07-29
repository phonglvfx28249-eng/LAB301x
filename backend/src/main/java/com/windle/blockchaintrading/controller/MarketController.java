package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.dto.response.CandleStickResponse;
import com.windle.blockchaintrading.dto.response.OrderResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.market.ChartDataService;
import com.windle.blockchaintrading.market.MarketDataService;
import com.windle.blockchaintrading.market.OMSLayer;
import com.windle.blockchaintrading.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private static final Logger log = LoggerFactory.getLogger(MarketController.class);
    private final ChartDataService chartDataService;
    private final MarketDataService marketDataService;

    @Autowired
    public MarketController(ChartDataService chartDataService, MarketDataService marketDataService) {
        this.chartDataService = chartDataService;
        this.marketDataService = marketDataService;
    }


    @GetMapping("/buyorderbook")
    public ResponseEntity<?> getBuyOrderBook() {
        try{
            List<OrderResponse> orderResponses = marketDataService.getTop10BuyOrders().stream().map(OrderResponse::fromEntity).toList();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            log.error("Error fetching buy order book: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/sellorderbook")
    public ResponseEntity<?> getSellOrderBook() {
        try{
            List<OrderResponse> orderResponses = marketDataService.getTop10SellOrders().stream().map(OrderResponse::fromEntity).toList();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            log.error("Error fetching sell order book: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/tradehistory")
    public ResponseEntity<?> getTradeHistory() {
        try{
            List<OrderResponse> orderResponses = marketDataService.getTop5Trades().stream().map(OrderResponse::fromEntity).toList();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            log.error("Error fetching trade history: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/chart")
    public ResponseEntity<?> getChart_data(@RequestParam String time) {
        try{
            List<CandleStickResponse> orderResponses = chartDataService.getCandleStickData(time);
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            log.error("Error fetching chart data: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/maxprice24h")
    public ResponseEntity<?> getMaxPrice24H() {
        try{
            BigDecimal maxPrice = marketDataService.getMaxPrice24h();
            return ResponseEntity.ok(maxPrice);
        } catch (Exception e) {
            log.error("Error fetching max price (24h): ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/minprice24h")
    public ResponseEntity<?> getMinPrice24H() {
        try{
            BigDecimal minPrice = marketDataService.getMinPrice24h();
            return ResponseEntity.ok(minPrice);
        } catch (Exception e) {
            log.error("Error fetching min price (24h): ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/volume24h")
    public ResponseEntity<?> getVolume24H() {
        try{
            long volume = marketDataService.getVolume24h();
            return ResponseEntity.ok(volume);
        } catch (Exception e) {
            log.error("Error fetching volume (24h): ", e);
            return ResponseEntity.status(500).build();
        }
    }

}
