package com.windle.blockchaintrading.service.impl;

import com.windle.blockchaintrading.dto.response.UserTradeResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.Trade;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.repository.TradeRepository;
import com.windle.blockchaintrading.repository.UserRepository;
import com.windle.blockchaintrading.service.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlockService blockService;

    @InjectMocks
    private TradeServiceImpl tradeService;

    private User buyer;
    private User seller;
    private Order buyOrder;
    private Order sellOrder;
    private Trade sampleTrade;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);

        seller = new User();
        seller.setId(2L);

        buyOrder = new Order();
        buyOrder.setId(10L);

        sellOrder = new Order();
        sellOrder.setId(20L);

        sampleTrade = new Trade();
        sampleTrade.setId(100L);
        sampleTrade.setBuyOrder(buyOrder);
        sampleTrade.setSellOrder(sellOrder);
        sampleTrade.setBuyer(buyer);
        sampleTrade.setSeller(seller);
        sampleTrade.setTradePrice(BigDecimal.valueOf(150));
        sampleTrade.setQuantity(BigDecimal.valueOf(2));
        sampleTrade.setTotalAmount(BigDecimal.valueOf(300));
    }

    @Test
    void executeTrade_shouldThrowExceptionForInvalidQuantityOrPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                tradeService.executeTrade(10L, 20L, 1L, 2L, BigDecimal.valueOf(100), BigDecimal.ZERO)
        );
        assertThrows(IllegalArgumentException.class, () ->
                tradeService.executeTrade(10L, 20L, 1L, 2L, BigDecimal.ZERO, BigDecimal.valueOf(1))
        );
    }

    @Test
    void executeTrade_shouldSaveTradeAndAddToMempool() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(sellOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trade result = tradeService.executeTrade(10L, 20L, 1L, 2L, BigDecimal.valueOf(150), BigDecimal.valueOf(2));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150), result.getTradePrice());
        assertEquals(BigDecimal.valueOf(2), result.getQuantity());
        assertEquals(BigDecimal.valueOf(300), result.getTotalAmount());
        verify(blockService).addTradeToMempool(result.getId());
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void getUserTradesPaginated_shouldMapToUserTradeResponseWithCorrectSide() {
        Page<Trade> page = new PageImpl<>(List.of(sampleTrade));
        when(tradeRepository.findUserTradeHistory(eq(1L), any())).thenReturn(page);

        Page<UserTradeResponse> response = tradeService.getUserTradesPaginated(1L, 0, 10);

        assertEquals(1, response.getContent().size());
        assertEquals("BUY", response.getContent().get(0).getSide());
    }

    @Test
    void getTradeByIdAndUserId_shouldThrowSecurityExceptionIfNotParticipant() {
        when(tradeRepository.findById(100L)).thenReturn(Optional.of(sampleTrade));

        assertThrows(SecurityException.class, () ->
                tradeService.getTradeByIdAndUserId(100L, 999L)
        );
    }

    @Test
    void getTradeByIdAndUserId_shouldReturnResponseForBuyer() {
        when(tradeRepository.findById(100L)).thenReturn(Optional.of(sampleTrade));

        UserTradeResponse response = tradeService.getTradeByIdAndUserId(100L, 1L);

        assertNotNull(response);
        assertEquals("BUY", response.getSide());
        assertEquals(100L, response.getTradeId());
    }

    @Test
    void getTradeByIdAndUserId_shouldReturnResponseForSeller() {
        when(tradeRepository.findById(100L)).thenReturn(Optional.of(sampleTrade));

        UserTradeResponse response = tradeService.getTradeByIdAndUserId(100L, 2L);

        assertNotNull(response);
        assertEquals("SELL", response.getSide());
    }
}
