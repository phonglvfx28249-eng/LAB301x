package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.BlockTransaction;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.service.BlockTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockTransactionServiceImpl implements BlockTransactionService {

    private final BlockTransactionRepository blockTransactionRepository;
    private final BlockRepository blockRepository;
    private final TradeRepository tradeRepository;

    @Autowired
    public BlockTransactionServiceImpl(BlockTransactionRepository blockTransactionRepository,
                                       BlockRepository blockRepository,
                                       TradeRepository tradeRepository) {
        this.blockTransactionRepository = blockTransactionRepository;
        this.blockRepository = blockRepository;
        this.tradeRepository = tradeRepository;
    }

    @Override
    public boolean tradeAlreadyInBlock(Long tradeId) {
        return blockTransactionRepository.existsByTradeId(tradeId);
    }

    @Override
    @Transactional
    public BlockTransaction addTradeToBlock(Long blockId, Long tradeId) {
        if (tradeAlreadyInBlock(tradeId)) {
            throw new IllegalStateException("Trade " + tradeId + " has already been recorded in a block");
        }

        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found with id: " + blockId));
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + tradeId));

        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setBlock(block);
        blockTransaction.setTrade(trade);

        return blockTransactionRepository.save(blockTransaction);
    }

    @Override
    public List<BlockTransaction> getAll() {
        return blockTransactionRepository.findAll();
    }

    @Override
    public BlockTransaction getById(Long id) {
        return blockTransactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<BlockTransaction> getByBlockId(Long blockId) {
        return blockTransactionRepository.findByBlockId(blockId);
    }

    @Override
    public BlockTransaction getByTradeId(Long tradeId) {
        return blockTransactionRepository.findByTradeId(tradeId).orElse(null);
    }

    @Override
    public List<BlockTransaction> findByBlockId(Long blockId) {
        return blockTransactionRepository.findByBlockId(blockId);
    }
}