package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.dto.response.OrderResponse;
import com.windle.blockchaintrading.dto.response.UserTradeResponse;
import com.windle.blockchaintrading.dto.response.WalletResponse;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.entity.WalletTransaction;
import com.windle.blockchaintrading.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user/resources")
public class UserResourcesController {
    private static final Logger log = LoggerFactory.getLogger(UserResourcesController.class);
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
            System.out.println("Fetching wallet resources for user: " + user.getId());

            WalletResponse wallet = walletService.getUserWalletResponse(user.getId());
            if(wallet == null){
                System.out.println("Wallet not found for user: " + user.getId());
                return ResponseEntity.status(404).body("Wallet not found for user");
            }
            // Proceed with fetching wallet resources for the authenticated user
            return ResponseEntity.ok(wallet);
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


    // get user page base on pagination
    @GetMapping("/trade")
    public ResponseEntity<?> getTradeResourcesByUser(Authentication authentication, @RequestParam int page){
        try{
            User user = (User) authentication.getPrincipal();
            // Fetch trade resources for the authenticated user
            int pageIndex = Math.max(0, page - 1); // Adjust for 0-based page index
            Page<UserTradeResponse> trades = tradeService.getUserTradesPaginated(user.getId(), pageIndex, 10);
            return ResponseEntity.ok(trades);
        } catch (Exception e) {
            log.error("Error occurred while fetching trade resources", e);
            return ResponseEntity.status(500).body("Error occurred while fetching trade resources");
        }

    }


    @GetMapping("/trade/{trade_id}")
    public ResponseEntity<?> getTradeResourceByTradeId(Authentication authentication,@PathVariable Long trade_id) {
        try {
            User user = (User) authentication.getPrincipal();
            // Fetch trade resource for the authenticated user by trade_id
            UserTradeResponse trade = tradeService.getTradeByIdAndUserId(trade_id, user.getId());
            if (trade == null) {
                return ResponseEntity.status(404).body("Trade not found for user");
            }
            return ResponseEntity.ok(trade);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while fetching trade resource");

        }
    }

    @GetMapping("/trade/total_quantity")
    public BigDecimal getTotalCoinsOwnedByUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println("Calculate coins for user:" + user.getId());
        BigDecimal quantity = tradeService.calculateTotalCoinsOwnedByUserId(user.getId());
        System.out.println("Total coins owned by user " + user.getId() + ": " + quantity);
        return quantity;
    }

}
