package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockIndex(Long blockIndex);

    Optional<Block> findByCurrentHash(String currentHash);

    boolean existsByCurrentHash(String currentHash);

    // Useful for grabbing the chain tip when computing the next block's previous_hash
    Optional<Block> findTopByOrderByBlockIndexDesc();
}