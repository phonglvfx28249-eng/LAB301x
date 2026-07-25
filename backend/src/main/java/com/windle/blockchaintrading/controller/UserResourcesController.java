package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.entity.WalletTransaction;
import com.windle.blockchaintrading.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getWalletResourcesByUser(){
    }


}
