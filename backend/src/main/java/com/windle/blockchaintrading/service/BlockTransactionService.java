package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.BlockTransaction;

import java.util.List;

public interface BlockTransactionService {

    boolean tradeAlreadyInBlock(Long tradeId);

    BlockTransaction addTradeToBlock(Long blockId, Long tradeId);

    List<BlockTransaction> getAll();

    BlockTransaction getById(Long id);

    List<BlockTransaction> getByBlockId(Long blockId);

    BlockTransaction getByTradeId(Long tradeId);

    List<BlockTransaction> findByBlockId(Long blockId);
}