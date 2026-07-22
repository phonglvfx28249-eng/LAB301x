package com.windle.blockchaintrading.repository;

import com.windle.blockchaintrading.entity.BlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockTransactionRepository extends JpaRepository<BlockTransaction, Long> {

    List<BlockTransaction> findByBlockId(Long blockId);

    Optional<BlockTransaction> findByTradeId(Long tradeId);

    boolean existsByTradeId(Long tradeId);
}