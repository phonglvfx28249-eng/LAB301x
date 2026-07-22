package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.Block;

import java.util.List;

public interface BlockService {

    boolean hashExists(String currentHash);

    Block createBlock(List<Long> tradeIds);

    List<Block> getAllBlocks();

    Block getBlockById(Long id);

    Block getBlockByIndex(Long blockIndex);

    Block getLatestBlock();

    boolean isChainValid();
}