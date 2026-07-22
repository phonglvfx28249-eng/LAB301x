package com.windle.blockchaintrading.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blocks")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "block_index", nullable = false, unique = true)
    private Long blockIndex;

    @Column(name = "previous_hash", length = 255)
    private String previousHash;

    @Column(name = "current_hash", length = 255, nullable = false, unique = true)
    private String currentHash;

    @Column(name = "merkle_root", length = 255)
    private String merkleRoot;

    @Column(name = "nonce")
    private Long nonce;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockTransaction> blockTransactions = new ArrayList<>();

    public Block() {
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(Long blockIndex) {
        this.blockIndex = blockIndex;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<BlockTransaction> getBlockTransactions() {
        return blockTransactions;
    }

    public void setBlockTransactions(List<BlockTransaction> blockTransactions) {
        this.blockTransactions = blockTransactions;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", blockIndex=" + blockIndex +
                ", previousHash='" + previousHash + '\'' +
                ", currentHash='" + currentHash + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", nonce=" + nonce +
                ", createdAt=" + createdAt +
                '}';
    }
}