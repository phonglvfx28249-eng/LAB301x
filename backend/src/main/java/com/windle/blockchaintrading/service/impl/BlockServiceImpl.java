package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.BlockTransaction;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is NOT a real mining/consensus chain - it's just a tamper-evident audit
 * trail. Each block hashes its own content plus the previous block's hash, so
 * if any row in the DB gets edited directly, isChainValid() will catch it.
 * No proof-of-work, no difficulty target, no nonce guessing.
 */
@Service
public class BlockServiceImpl implements BlockService {

    private static final String GENESIS_PREVIOUS_HASH = "0";

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
    public boolean hashExists(String currentHash) {
        return blockRepository.existsByCurrentHash(currentHash);
    }

    @Override
    public Block createBlock(List<Long> tradeIds) {
        // Fetch trades and create block transactions
        if (tradeIds == null || tradeIds.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a block with no trades");
        }

        List<Trade> trades = tradeRepository.findAllById(tradeIds);

        Block previousBlock = getLatestBlock();
        Long newBlockIndex = (previousBlock == null) ? 0L : previousBlock.getBlockIndex() + 1;
        String previousHash = (previousBlock == null) ? GENESIS_PREVIOUS_HASH : previousBlock.getCurrentHash();
        String merkleRoot = createMerkleTree(trades);

        Block newBlock = new Block();
        // set new block data
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
            String recalculatedHash = hashData(currentBlock.getPreviousHash() + createMerkleTree(getTradesForBlock(currentBlock)) + +currentBlock.getBlockIndex());
            if (!currentBlock.getCurrentHash().equals(recalculatedHash)) {
                return false;
            }
        }
        return true;
    }


    // some of the basic of hashing function and blockchain
    private String createMerkleTree(List<Trade> trades) {
        // simple implementation: concatenate trade IDs and hash them
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

    // record to block_transaction, identify which trade included in each block
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