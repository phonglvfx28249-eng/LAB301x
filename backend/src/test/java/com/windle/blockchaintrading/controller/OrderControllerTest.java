package com.windle.blockchaintrading.controller;

import com.windle.blockchaintrading.dto.request.OrderRequest;
import com.windle.blockchaintrading.dto.response.OrderProfitResponse;
import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.market.OMSLayer;
import com.windle.blockchaintrading.service.OrderService;
import com.windle.blockchaintrading.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;
    @Mock
    private OMSLayer omsLayer;
    @Mock
    private WalletService walletService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
    }

    @Test
    void sendOrder_shouldReturnSuccessResponse() {
        OrderRequest request = new OrderRequest(null, 1L, "BUY", "LIMIT",BigDecimal.valueOf(1.2), BigDecimal.valueOf(90));
        Order order = new Order();
        order.setId(10L);

        when(authentication.getPrincipal()).thenReturn(sampleUser);
        when(orderService.placeOrder(1L, Order.Side.BUY, Order.OrderType.LIMIT, BigDecimal.valueOf(1.2), BigDecimal.valueOf(90)))
                .thenReturn(order);

        ResponseEntity<?> response = orderController.sendOrder(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order sent successfully", response.getBody());
    }

    @Test
    void closeOrder_shouldReturnBadRequestIfNotOwned() {
        OrderRequest request = new OrderRequest(99L, 0L, "BUY", "SHORT", BigDecimal.valueOf(1.2), BigDecimal.valueOf(90));

        when(authentication.getPrincipal()).thenReturn(sampleUser);
        when(orderService.checkOrderOwnByUser(99L, 1L)).thenReturn(false);

        ResponseEntity<?> response = orderController.closeOrder(request, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeOrder_shouldCloseOrderWhenOwned() {
        OrderRequest request = new OrderRequest(10L, 1L, "BUY", "SHORT", BigDecimal.valueOf(1.2), BigDecimal.valueOf(90));

        when(authentication.getPrincipal()).thenReturn(sampleUser);
        when(orderService.checkOrderOwnByUser(10L, 1L)).thenReturn(true);

        ResponseEntity<?> response = orderController.closeOrder(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).closeOrder(10L);
    }

    @Test
    void getOrder_shouldReturnNoActiveOrdersFoundWhenEmpty() {
        when(authentication.getPrincipal()).thenReturn(sampleUser);
        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of());

        ResponseEntity<?> response = orderController.getOrder(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No active orders found", response.getBody());
    }

    @Test
    void getOrder_shouldReturnOrderProfitResponseWhenActiveOrderExists() {
        Order activeOrder = new Order();
        activeOrder.setId(10L);
        activeOrder.setStatus(Order.OrderStatus.FILLED);

        OrderProfitResponse profitResponse = new OrderProfitResponse(
                10L, BigDecimal.valueOf(100), BigDecimal.valueOf(120),
                BigDecimal.valueOf(2), "BUY", "FILLED", BigDecimal.valueOf(40)
        );

        when(authentication.getPrincipal()).thenReturn(sampleUser);
        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(activeOrder));
        when(omsLayer.calculateSingleOrderProfit(10L)).thenReturn(profitResponse);

        ResponseEntity<?> response = orderController.getOrder(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profitResponse, response.getBody());
    }
}
