package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.dto.request.OrderRequest;
import com.windle.blockchaintrading.dto.response.OrderProfitResponse;
import com.windle.blockchaintrading.dto.response.OrderResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.market.OMSLayer;
import com.windle.blockchaintrading.service.OrderService;
import com.windle.blockchaintrading.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class    OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final OMSLayer oMSLayer;
    private final WalletService walletService;

    @Autowired
    public OrderController(OrderService orderService, OMSLayer oMSLayer, WalletService walletService) {
        this.orderService = orderService;
        this.oMSLayer = oMSLayer;
        this.walletService = walletService;
    }

    @PostMapping("/send_order")
    public ResponseEntity<?> sendOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                Order order = orderService.placeOrder(user.getId(), Order.Side.valueOf(orderRequest.side()), Order.OrderType.valueOf(orderRequest.type()), orderRequest.price(), orderRequest.quantity());
                return ResponseEntity.ok("Order sent successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid user");
            }
        } catch (Exception e) {
            log.error("Error sending order: ", e);
            return ResponseEntity.internalServerError().body("Error sending order");
        }
    }

    @PostMapping("/close_order")
    public ResponseEntity<?> closeOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                // check if the order is owned by the user
                if (orderService.checkOrderOwnByUser(orderRequest.id(), user.getId())) {
                    orderService.closeOrder(orderRequest.id());

                    return ResponseEntity.ok("Order closed successfully");
                } else {
                    return ResponseEntity.badRequest().body("Order not found or not owned by user");
                }
            } else {
                return ResponseEntity.badRequest().body("Invalid user");
            }
        } catch (Exception e) {
            log.error("Error closing order: ", e);
            return ResponseEntity.internalServerError().body("Error closing order");
        }
    }

    @GetMapping("/get_order")
    public ResponseEntity<?> getOrder(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                return ResponseEntity.badRequest().body("Invalid user");
            }

            // Fetch user's orders and filter for active/filled positions
            List<Order> activeOrders = orderService.getOrdersByUserId(user.getId())
                    .stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.PENDING
                            || order.getStatus() == Order.OrderStatus.OPEN
                            || order.getStatus() == Order.OrderStatus.PARTIAL
                            || order.getStatus() == Order.OrderStatus.FILLED)
                    .toList();

            if (activeOrders.isEmpty()) {
                return ResponseEntity.ok().body("No active orders found");
            }

            Order activeOrder = activeOrders.getFirst();

            // Calculate PnL and build the OrderProfitResponse record
            OrderProfitResponse response = oMSLayer.calculateSingleOrderProfit(activeOrder.getId());


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting order PnL: ", e);
            return ResponseEntity.internalServerError().body("Error getting order");
        }
    }


}

