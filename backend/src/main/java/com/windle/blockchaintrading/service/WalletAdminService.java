package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.WalletAdminDTO;
import com.windle.blockchaintrading.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletAdminService {

    private final WalletRepository walletRepository;

    public WalletAdminService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public PageResponse<WalletAdminDTO> list(int page, int size, String search) {
        Page<Object[]> rows = walletRepository.searchWithUsername(
            search, PageRequest.of(page, size)
        );

        Page<WalletAdminDTO> mapped = rows.map(row -> new WalletAdminDTO(
            ((Number) row[0]).longValue(),          // userId
            (String) row[1],                         // username
            ((Number) row[2]).longValue(),           // walletId
            (BigDecimal) row[3],                      // availableBalance
            (BigDecimal) row[4]                       // lockedBalance
        ));

        return PageResponse.of(mapped);
    }
}
