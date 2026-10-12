package com.fynxt.trading.repository;

import com.fynxt.trading.entity.OrderEntity;
import com.fynxt.trading.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    long countByTraderIdAndStatus(String traderId, OrderStatus status);

    Optional<OrderEntity> findByIdAndTraderId(UUID id, String traderId);

    List<OrderEntity> findByTraderIdAndStockAndStatus(String traderId, String stock, OrderStatus status);
}
