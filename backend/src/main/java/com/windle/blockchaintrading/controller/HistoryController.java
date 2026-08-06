package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.dto.response.TradeHistoryResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);

    private final OrderService orderService;
    private final TradeRepository tradeRepository;

    @Autowired
    public HistoryController(OrderService orderService, TradeRepository tradeRepository) {
        this.orderService = orderService;
        this.tradeRepository = tradeRepository;
    }

    @GetMapping("/trades")
    public ResponseEntity<?> getUserTradeHistory(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                return ResponseEntity.badRequest().body("Invalid user authentication");
            }

            // 1. Fetch live market price to compute floating PnL and ROI %
            BigDecimal currentMarketPrice = tradeRepository.findCurrentMarketPrice()
                    .orElse(BigDecimal.ZERO);

            // 2. Fetch user's orders (e.g., PENDING, OPEN, PARTIAL, FILLED, CLOSED)
            List<Order> userOrders = orderService.getOrdersByUserId(user.getId());

            if (userOrders.isEmpty()) {
                return ResponseEntity.ok(List.of()); // Returns empty array [] for React
            }

            // 3. Map orders directly to the TradeHistoryResponse DTO expected by React
            List<TradeHistoryResponse> responseList = userOrders.stream()
                    .map(order -> TradeHistoryResponse.fromOrder(order, currentMarketPrice))
                    .toList();

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("Error fetching trade history for user: ", e);
            return ResponseEntity.internalServerError().body("Error retrieving trade history");
        }
    }
}