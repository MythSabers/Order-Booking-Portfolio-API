package com.fynxt.trading.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "portfolio_positions", uniqueConstraints = @UniqueConstraint(name = "uq_position_trader_stock", columnNames = {"trader_id", "stock"}))
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trader_id", nullable = false, length = 64)
    private String traderId;

    @Column(nullable = false, length = 16)
    private String stock;

    @Column(nullable = false, length = 32)
    private String sector;

    @Column(nullable = false)
    private long quantity;

    public void changeQuantity(long delta) {
        long next = Math.addExact(quantity, delta);
        if (next < 0)
            throw new IllegalArgumentException("Holding quantity cannot become negative");
        quantity = next;
    }

}
