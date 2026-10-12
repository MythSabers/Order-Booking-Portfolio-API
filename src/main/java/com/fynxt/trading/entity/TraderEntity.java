package com.fynxt.trading.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "traders")
public class TraderEntity {
    @Id
    @Column(name = "trader_id", length = 64)
    private String traderId;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
