package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceImplTest {

    @Mock
    private BlockRepository blockRepository;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private BlockTransactionRepository blockTransactionRepository;

    @InjectMocks
    private BlockServiceImpl blockService;

    private Trade sampleTrade;

    @BeforeEach
    void setUp() {
        sampleTrade = new Trade();
        sampleTrade.setId(100L);
        sampleTrade.setTotalAmount(BigDecimal.valueOf(500));
    }

    @Test
    void addTradeToMempool_shouldNotThrowException() {
        assertDoesNotThrow(() -> blockService.addTradeToMempool(100L));
    }

    @Test
    void createBlock_shouldThrowExceptionForEmptyTrades() {
        assertThrows(IllegalArgumentException.class, () -> blockService.createBlock(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> blockService.createBlock(null));
    }

    @Test
    void createBlock_shouldCreateGenesisBlockWhenNoPreviousBlockExists() {
        when(tradeRepository.findAllById(List.of(100L))).thenReturn(List.of(sampleTrade));
        when(blockRepository.findTopByOrderByBlockIndexDesc()).thenReturn(Optional.empty());
        when(blockRepository.save(any(Block.class))).thenAnswer(i -> i.getArgument(0));

        Block block = blockService.createBlock(List.of(100L));

        assertNotNull(block);
        assertEquals(0L, block.getBlockIndex());
        assertEquals("0", block.getPreviousHash());
        assertNotNull(block.getCurrentHash());
        verify(blockTransactionRepository).saveAll(any());
    }

    @Test
    void createBlock_shouldIncrementIndexFromPreviousBlock() {
        Block prev = new Block();
        prev.setBlockIndex(5L);
        prev.setCurrentHash("hash123");

        when(tradeRepository.findAllById(List.of(100L))).thenReturn(List.of(sampleTrade));
        when(blockRepository.findTopByOrderByBlockIndexDesc()).thenReturn(Optional.of(prev));
        when(blockRepository.save(any(Block.class))).thenAnswer(i -> i.getArgument(0));

        Block block = blockService.createBlock(List.of(100L));

        assertEquals(6L, block.getBlockIndex());
        assertEquals("hash123", block.getPreviousHash());
    }

    @Test
    void scheduledBlockForge_shouldSkipWhenMempoolEmpty() {
        blockService.scheduledBlockForge();
        verifyNoInteractions(blockRepository);
    }
}
