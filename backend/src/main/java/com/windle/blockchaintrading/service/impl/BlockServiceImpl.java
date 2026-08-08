package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.BlockTransaction;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.service.BlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockServiceImpl implements BlockService {

    private static final Logger log = LoggerFactory.getLogger(BlockServiceImpl.class);
    private static final String GENESIS_PREVIOUS_HASH = "0";

    // Thread-safe mempool storing trade IDs waiting to be mined into a block
    private final List<Long> mempool = Collections.synchronizedList(new ArrayList<>());

    private final BlockRepository blockRepository;
    private final TradeRepository tradeRepository;
    private final BlockTransactionRepository blockTransactionRepository;

    @Autowired
    public BlockServiceImpl(BlockRepository blockRepository,
                            TradeRepository tradeRepository,
                            BlockTransactionRepository blockTransactionRepository) {
        this.blockRepository = blockRepository;
        this.tradeRepository = tradeRepository;
        this.blockTransactionRepository = blockTransactionRepository;
    }


    @Override
    public void addTradeToMempool(Long tradeId) {
        mempool.add(tradeId);
        log.info("Trade #{} added to mempool. Current mempool size: {}", tradeId, mempool.size());
    }


    // create block after a 3000ms interval
    @Scheduled(fixedRate = 3000)
    @Transactional
    public void scheduledBlockForge() {
        if (mempool.isEmpty()) {
            return; // Skip block creation if no trades occurred in this 3-second cycle
        }

        // Drain trade IDs atomically from mempool
        List<Long> tradesToMine;
        synchronized (mempool) {
            tradesToMine = new ArrayList<>(mempool);
            mempool.clear();
        }

        try {
            Block block = createBlock(tradesToMine);
            log.info("New Block #{} created! Hash: {} | Contains {} trades",
                    block.getBlockIndex(), block.getCurrentHash(), tradesToMine.size());
        } catch (Exception e) {
            log.error("Failed to forge scheduled block: ", e);
            // Re-queue trades back into mempool if block creation failed
            mempool.addAll(tradesToMine);
        }
    }

    @Override
    public boolean hashExists(String currentHash) {
        return blockRepository.existsByCurrentHash(currentHash);
    }

    @Override
    @Transactional
    public Block createBlock(List<Long> tradeIds) {
        if (tradeIds == null || tradeIds.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a block with no trades");
        }

        List<Trade> trades = tradeRepository.findAllById(tradeIds);

        Block previousBlock = getLatestBlock();
        Long newBlockIndex = (previousBlock == null) ? 0L : previousBlock.getBlockIndex() + 1;
        String previousHash = (previousBlock == null) ? GENESIS_PREVIOUS_HASH : previousBlock.getCurrentHash();
        String merkleRoot = createMerkleTree(trades);

        Block newBlock = new Block();
        newBlock.setBlockIndex(newBlockIndex);
        newBlock.setPreviousHash(previousHash);
        newBlock.setCurrentHash(hashData(previousHash + merkleRoot + newBlockIndex));
        newBlock.setNonce(0L); // No proof-of-work, so nonce is just 0

        Block saveBlock = blockRepository.save(newBlock);

        recordBlockTransactions(saveBlock, trades);

        return saveBlock;
    }

    @Override
    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    @Override
    public Block getBlockById(Long id) {
        return blockRepository.findById(id).orElse(null);
    }

    @Override
    public Block getBlockByIndex(Long blockIndex) {
        return blockRepository.findByBlockIndex(blockIndex).orElse(null);
    }

    @Override
    public Block getLatestBlock() {
        return blockRepository.findTopByOrderByBlockIndexDesc().orElse(null);
    }

    @Override
    public boolean isChainValid() {
        List<Block> blocks = blockRepository.findAll();
        blocks.sort(Comparator.comparingLong(Block::getBlockIndex));
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);
            if (!currentBlock.getPreviousHash().equals(previousBlock.getCurrentHash())) {
                return false;
            }
            // Fixed typo: Removed extra '+' sign before currentBlock.getBlockIndex()
            String recalculatedHash = hashData(currentBlock.getPreviousHash() + createMerkleTree(getTradesForBlock(currentBlock)) + currentBlock.getBlockIndex());
            if (!currentBlock.getCurrentHash().equals(recalculatedHash)) {
                return false;
            }
        }
        return true;
    }

    // Merkle tree hashing function
    private String createMerkleTree(List<Trade> trades) {
        StringBuilder merkleTree = new StringBuilder();
        for (Trade trade : trades) {
            merkleTree.append(hashData(trade.getId() + ":" + trade.getTotalAmount().toString()));
        }
        return hashData(merkleTree.toString());
    }

    private String hashData(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Record to block_transaction table each block contain trades
    private void recordBlockTransactions(Block block, List<Trade> trades) {
        List<BlockTransaction> blockTransactions = trades.stream()
                .map(trade -> {
                    BlockTransaction bt = new BlockTransaction();
                    bt.setBlock(block);
                    bt.setTrade(trade);
                    return bt;
                })
                .collect(Collectors.toList());
        blockTransactionRepository.saveAll(blockTransactions);
    }

    private List<Trade> getTradesForBlock(Block block) {
        return blockTransactionRepository.findByBlockId(block.getId()).stream()
                .map(BlockTransaction::getTrade)
                .collect(Collectors.toList());
    }
}