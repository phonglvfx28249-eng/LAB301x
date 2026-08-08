package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.BlockAdminDTO;
import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainAdminServiceTest {

    @Mock
    private BlockRepository blockRepository;
    @Mock
    private BlockTransactionRepository blockTransactionRepository;

    @InjectMocks
    private BlockchainAdminService blockchainAdminService;

    @Test
    void list_shouldReturnPaginatedBlockAdminDTO() {
        Block block = new Block();
        block.setId(1L);
        block.setBlockIndex(0L);
        block.setPreviousHash("0");
        block.setCurrentHash("hash0");

        Page<Block> page = new PageImpl<>(List.of(block));

        when(blockRepository.findAllByOrderByBlockIndexDesc(any(PageRequest.class))).thenReturn(page);
        when(blockTransactionRepository.countByBlockId(1L)).thenReturn(Math.toIntExact(5L));

        PageResponse<BlockAdminDTO> response = blockchainAdminService.list(0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(5, response.getContent().get(0).getTransactionCount());
    }
}
