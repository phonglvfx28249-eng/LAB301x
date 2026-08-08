package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.TradeAdminDTO;
import com.windle.blockchaintrading.service.TradeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/trades")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTradeController {

    private final TradeAdminService tradeAdminService;

    @Autowired
    public AdminTradeController(TradeAdminService tradeAdminService) {
        this.tradeAdminService = tradeAdminService;
    }

    @GetMapping
    public PageResponse<TradeAdminDTO> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return tradeAdminService.list(page, size);
    }
}
