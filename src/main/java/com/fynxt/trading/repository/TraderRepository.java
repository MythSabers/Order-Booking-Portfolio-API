package com.fynxt.trading.repository;

import com.fynxt.trading.entity.TraderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraderRepository extends JpaRepository<TraderEntity, String> {
    @Modifying
    @Query(value = "INSERT INTO traders(trader_id) VALUES (:id) ON CONFLICT (trader_id) DO NOTHING", nativeQuery = true)
    void createIfAbsent(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TraderEntity t where t.traderId = :id")
    Optional<TraderEntity> lockById(@Param("id") String id);
}
