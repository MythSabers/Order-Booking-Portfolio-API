package com.fynxt.trading.entity;

import com.fynxt.trading.enums.OrderSide;
import com.fynxt.trading.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_trader_status", columnList = "trader_id,status"),
        @Index(name = "idx_orders_trader_stock_status", columnList = "trader_id,stock,status")
})
public class OrderEntity {
    @Id
    private UUID id;
    @Column(name = "trader_id", nullable = false, length = 64)
    private String traderId;
    @Column(nullable = false, length = 16)
    private String stock;
    @Column(nullable = false, length = 32)
    private String sector;
    @Column(nullable = false)
    private long quantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private OrderSide side;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private OrderStatus status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrderEntity() {
    }

    public OrderEntity(String traderId, String stock, String sector, long quantity, OrderSide side) {
        this.id = UUID.randomUUID();
        this.traderId = traderId;
        this.stock = stock;
        this.sector = sector;
        this.quantity = quantity;
        this.side = side;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void fill() {
        this.status = OrderStatus.FILLED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getStock() {
        return stock;
    }

    public String getSector() {
        return sector;
    }

    public long getQuantity() {
        return quantity;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
