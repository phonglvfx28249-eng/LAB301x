package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.entity.BlockTransaction;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockTransactionServiceImplTest {

    @Mock
    private BlockTransactionRepository blockTransactionRepository;
    @Mock
    private BlockRepository blockRepository;
    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private BlockTransactionServiceImpl blockTransactionService;

    private Block sampleBlock;
    private Trade sampleTrade;

    @BeforeEach
    void setUp() {
        sampleBlock = new Block();
        sampleBlock.setId(1L);

        sampleTrade = new Trade();
        sampleTrade.setId(10L);
    }

    @Test
    void tradeAlreadyInBlock_shouldReturnRepositoryStatus() {
        when(blockTransactionRepository.existsByTradeId(10L)).thenReturn(true);
        assertTrue(blockTransactionService.tradeAlreadyInBlock(10L));
    }

    @Test
    void addTradeToBlock_shouldThrowExceptionIfAlreadyInBlock() {
        when(blockTransactionRepository.existsByTradeId(10L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                blockTransactionService.addTradeToBlock(1L, 10L)
        );
    }

    @Test
    void addTradeToBlock_shouldSaveAndReturnBlockTransaction() {
        when(blockTransactionRepository.existsByTradeId(10L)).thenReturn(false);
        when(blockRepository.findById(1L)).thenReturn(Optional.of(sampleBlock));
        when(tradeRepository.findById(10L)).thenReturn(Optional.of(sampleTrade));
        when(blockTransactionRepository.save(any(BlockTransaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        BlockTransaction bt = blockTransactionService.addTradeToBlock(1L, 10L);

        assertNotNull(bt);
        assertEquals(sampleBlock, bt.getBlock());
        assertEquals(sampleTrade, bt.getTrade());
    }

    @Test
    void getByBlockId_shouldReturnList() {
        BlockTransaction bt = new BlockTransaction();
        when(blockTransactionRepository.findByBlockId(1L)).thenReturn(List.of(bt));

        List<BlockTransaction> result = blockTransactionService.getByBlockId(1L);

        assertEquals(1, result.size());
    }
}
