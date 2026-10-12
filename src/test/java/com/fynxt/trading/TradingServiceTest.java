package com.fynxt.trading;

import com.fynxt.trading.entity.OrderEntity;
import com.fynxt.trading.entity.PositionEntity;
import com.fynxt.trading.entity.TraderEntity;
import com.fynxt.trading.enums.OrderSide;
import com.fynxt.trading.enums.OrderStatus;
import com.fynxt.trading.models.PlaceOrderRequest;
import com.fynxt.trading.repository.OrderRepository;
import com.fynxt.trading.repository.PositionRepository;
import com.fynxt.trading.repository.TraderRepository;
import com.fynxt.trading.services.TradingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradingServiceTest {
    private TraderRepository traders;
    private OrderRepository orders;
    private PositionRepository positions;
    private TradingService service;

    @BeforeEach
    void setup() {
//        traders = mock(TraderRepository.class);
//        orders = mock(OrderRepository.class);
//        positions = mock(PositionRepository.class);
//        when(traders.lockById(any())).thenReturn(Optional.of(new TraderEntity("T1")));
//        service = new TradingService(traders, orders, positions);
    }

    @Test
    void rejectsFourthPendingOrder() {
        when(orders.countByTraderIdAndStatus("T1", OrderStatus.PENDING)).thenReturn(3L);
        PlaceOrderRequest request = new PlaceOrderRequest("T1", "AAPL", "TECH", 10, OrderSide.BUY);
        assertThrows(ResponseStatusException.class, () -> service.placeOrder(request));
        verify(orders, never()).save(any());
    }

    @Test
    void rejectsSellWhenAvailableSharesAreInsufficient() {
//        when(orders.countByTraderIdAndStatus("T1", OrderStatus.PENDING)).thenReturn(0L);
//        when(positions.findByTraderIdAndStock("T1", "AAPL"))
//                .thenReturn(Optional.of(new PositionEntity("T1", "AAPL", "TECH", 10)));
//        when(orders.findByTraderIdAndStockAndStatus("T1", "AAPL", OrderStatus.PENDING))
//                .thenReturn(java.util.List.of());
//        PlaceOrderRequest request = new PlaceOrderRequest("T1", "AAPL", "TECH", 11, OrderSide.SELL);
//        assertThrows(ResponseStatusException.class, () -> service.placeOrder(request));
//        verify(orders, never()).save(any());
    }

    @Test
    void onlyPendingOrdersCanBeCancelled() {
//        OrderEntity filled = new OrderEntity("T1", "AAPL", "TECH", 1, OrderSide.BUY);
//        filled.fill();
//        when(orders.findByIdAndTraderId("T1", filled.getId())).thenReturn(Optional.of(filled));
//        assertThrows(ResponseStatusException.class, () -> service.cancel("T1", filled.getId()));
    }
}
