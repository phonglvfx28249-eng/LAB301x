package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.BlockAdminDTO;
import com.windle.blockchaintrading.entity.Block;
import com.windle.blockchaintrading.repository.BlockRepository;
import com.windle.blockchaintrading.repository.BlockTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BlockchainAdminService {

    private final BlockRepository blockRepository;
    private final BlockTransactionRepository blockTransactionRepository;

    public BlockchainAdminService(BlockRepository blockRepository,
                                   BlockTransactionRepository blockTransactionRepository) {
        this.blockRepository = blockRepository;
        this.blockTransactionRepository = blockTransactionRepository;
    }

    public PageResponse<BlockAdminDTO> list(int page, int size) {
        Page<Block> result = blockRepository.findAllByOrderByBlockIndexDesc(
            PageRequest.of(page, size)
        );
        Page<BlockAdminDTO> mapped = result.map(b -> BlockAdminDTO.from(
            b, blockTransactionRepository.countByBlockId(b.getId())
        ));
        return PageResponse.of(mapped);
    }
}
