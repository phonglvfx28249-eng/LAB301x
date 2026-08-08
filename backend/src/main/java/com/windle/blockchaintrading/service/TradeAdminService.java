package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.TradeAdminDTO;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.TradeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TradeAdminService {

    private final TradeRepository tradeRepository;

    public TradeAdminService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public PageResponse<TradeAdminDTO> list(int page, int size) {
        Page<Trade> result = tradeRepository.findAllByOrderByCreatedAtDesc(
            PageRequest.of(page, size)
        );
        return PageResponse.of(result.map(TradeAdminDTO::from));
    }

    /** Used by wallet management's "Trade History" link for a specific user. */
    public PageResponse<TradeAdminDTO> listForUser(Long userId, int page, int size) {

        Page<Trade> result = (Page<Trade>) tradeRepository.findByBuyerIdOrSellerId(
            userId, userId, PageRequest.of(page, size)
        );
        return PageResponse.of(result.map(TradeAdminDTO::from));
    }
}
