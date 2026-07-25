package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.dto.response.OrderResponse;
import com.windle.blockchaintrading.dto.response.WalletResponse;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.entity.WalletTransaction;
import com.windle.blockchaintrading.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/user/resources")
public class UserResourcesController {
    // create service injection
    private UserService userService;
    private WalletService walletService;
    private WalletTransaction walletTransaction;
    private TradeService tradeService;
    private OrderService orderService;
    private BlockService blockService;
    private BlockTransactionService blockTransactionService;

    @Autowired
    public UserResourcesController(UserService userService, WalletService walletService, TradeService tradeService,
                                   OrderService orderService, BlockService blockService, BlockTransactionService blockTransactionService) {
        this.userService = userService;
        this.walletService = walletService;
        this.tradeService = tradeService;
        this.orderService = orderService;
        this.blockService = blockService;
        this.blockTransactionService = blockTransactionService;
    }


    @GetMapping("/wallet")
    public ResponseEntity<?> getWalletResourcesByUser(Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            Wallet wallet = walletService.getWalletByUserId(user.getId());
            if(wallet == null){
                return ResponseEntity.status(404).body("Wallet not found for user");
            }
            // Proceed with fetching wallet resources for the authenticated user
            return ResponseEntity.ok(WalletResponse.fromEntity(wallet));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while fetching wallet resources");
        }

    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrderResourcesByUser(Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            // Fetch order resources for the authenticated user
            List<OrderResponse> orders = orderService.getOrdersByUserId(user.getId()).stream().map(OrderResponse::fromEntity).toList();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while fetching order resources");
        }
    }




}
