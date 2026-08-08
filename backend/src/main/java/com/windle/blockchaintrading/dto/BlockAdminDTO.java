package com.windle.blockchaintrading.dto;


import com.windle.blockchaintrading.entity.Block;

import java.time.LocalDateTime;

public class BlockAdminDTO {

    private Long id;
    private Long blockIndex;
    private String previousHash;
    private String currentHash;
    private String merkleRoot;
    private Long nonce;
    private LocalDateTime createdAt;
    private int transactionCount; // populated in service via block_transactions count

    public static BlockAdminDTO from(Block b, int txCount) {
        BlockAdminDTO dto = new BlockAdminDTO();
        dto.id = b.getId();
        dto.blockIndex = b.getBlockIndex();
        dto.previousHash = b.getPreviousHash();
        dto.currentHash = b.getCurrentHash();
        dto.merkleRoot = b.getMerkleRoot();
        dto.nonce = b.getNonce();
        dto.createdAt = b.getCreatedAt();
        dto.transactionCount = txCount;
        return dto;
    }

    public Long getId() { return id; }
    public Long getBlockIndex() { return blockIndex; }
    public String getPreviousHash() { return previousHash; }
    public String getCurrentHash() { return currentHash; }
    public String getMerkleRoot() { return merkleRoot; }
    public Long getNonce() { return nonce; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getTransactionCount() { return transactionCount; }
}
