package com.fynxt.trading.models;

import com.fynxt.trading.entity.OrderEntity;
import com.fynxt.trading.enums.OrderSide;
import com.fynxt.trading.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID orderId, String traderId, String stock, String sector,
                            long quantity, OrderSide side, OrderStatus status,
                            Instant createdAt, Instant updatedAt) {
    public static OrderResponse from(OrderEntity o) {
        return new OrderResponse(
                o.getId(),
                o.getTraderId(),
                o.getStock(),
                o.getSector(),
                o.getQuantity(),
                o.getSide(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
