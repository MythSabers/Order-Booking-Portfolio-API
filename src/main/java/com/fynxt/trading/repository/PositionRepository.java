package com.fynxt.trading.repository;

import com.fynxt.trading.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    List<PositionEntity> findByTraderId(String traderId);

    Optional<PositionEntity> findByTraderIdAndStock(String traderId, String stock);
}
