package com.fynxt.trading.services;

import com.fynxt.trading.repository.OrderRepository;
import com.fynxt.trading.repository.PositionRepository;
import com.fynxt.trading.repository.TraderRepository;
import com.fynxt.trading.entity.OrderEntity;
import com.fynxt.trading.entity.PositionEntity;
import com.fynxt.trading.enums.OrderSide;
import com.fynxt.trading.enums.OrderStatus;
import com.fynxt.trading.models.*;
import com.fynxt.trading.utils.OverlapAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradingService {
    private static final long MAX_PENDING = 3;
    private final TraderRepository traders;
    private final OrderRepository orders;
    private final PositionRepository positions;


    private void lockTrader(String traderId) {
        traders.createIfAbsent(traderId);
        traders.lockById(traderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not lock trader"));
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        String traderId = request.traderId().trim();
        String stock = normalize(request.stock());
        String sector = request.sector().trim().toUpperCase(Locale.ROOT);
        lockTrader(traderId); // serializes all portfolio/order decisions for this trader
        if (orders.countByTraderIdAndStatus(traderId, OrderStatus.PENDING) >= MAX_PENDING)
            throw conflict("A trader cannot have more than 3 PENDING orders.");

        if (request.side() == OrderSide.SELL) {
            long held = positions.findByTraderIdAndStock(traderId, stock)
                    .map(PositionEntity::getQuantity)
                    .orElse(0L);
            long alreadyReserved = orders.findByTraderIdAndStockAndStatus(traderId, stock, OrderStatus.PENDING)
                    .stream()
                    .filter(o -> o.getSide() == OrderSide.SELL)
                    .mapToLong(OrderEntity::getQuantity)
                    .sum();
            if (held - alreadyReserved < request.quantity())
                throw conflict("Insufficient available shares to SELL " + stock + ". Held=" + held +
                        ", reserved by pending sells=" + alreadyReserved + ", requested=" + request.quantity());
        }
        return OrderResponse.from(orders.save(new OrderEntity(traderId, stock, sector, request.quantity(), request.side())));
    }

    @Transactional
    public OrderResponse fill(String traderId, UUID orderId) {
        lockTrader(traderId);
        OrderEntity order = ownedOrder(traderId, orderId);
        requirePending(order, "filled");
        PositionEntity position = positions.findByTraderIdAndStock(traderId, order.getStock()).orElse(null);
        if (order.getSide() == OrderSide.BUY) {
            if (position == null)
                positions.save(
                        PositionEntity.builder()
                                .traderId(traderId)
                                .stock(order.getStock())
                                .sector(order.getSector())
                                .quantity(order.getQuantity())
                                .build()
                );
            else {
                ensureSectorMatches(position, order.getSector());
                position.changeQuantity(order.getQuantity());
            }
        } else {
            if (position == null || position.getQuantity() < order.getQuantity())
                throw conflict("Cannot fill SELL: holdings are insufficient.");
            position.changeQuantity(-order.getQuantity());
        }
        order.fill();
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancel(String traderId, UUID orderId) {
        lockTrader(traderId);
        OrderEntity order = ownedOrder(traderId, orderId);
        requirePending(order, "cancelled");
        order.cancel();
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public PortfolioResponse portfolio(String traderId) {
        List<PositionEntity> holdings = positions.findByTraderId(traderId);
        Map<String, Long> byStock = new HashMap<>();
        Map<String, Long> bySector = new HashMap<>();
        for (PositionEntity p : holdings) {
            if (p.getQuantity() <= 0)
                continue;
            byStock.put(p.getStock(), p.getQuantity());
            bySector.merge(p.getSector(), p.getQuantity(), Math::addExact);
        }
        return new PortfolioResponse(traderId, byStock, bySector);
    }

    @Transactional
    public PortfolioResponse addHolding(String traderId, AddHoldingRequest request) {
        lockTrader(traderId);
        String stock = normalize(request.stock());
        String sector = request.sector().trim().toUpperCase(Locale.ROOT);
        PositionEntity existing = positions.findByTraderIdAndStock(traderId, stock).orElse(null);
        if (existing == null)
            positions.save(
                    PositionEntity.builder()
                            .traderId(traderId)
                            .stock(stock)
                            .sector(sector)
                            .quantity(request.quantity())
                            .build()
            );
        else {
            ensureSectorMatches(existing, sector);
            existing.changeQuantity(request.quantity());
        }
        return portfolio(traderId);
    }

    @Transactional(readOnly = true)
    public OverlapResponse overlap(String traderId) {
        return OverlapAnalyzer.analyze(positions.findByTraderId(traderId)
                .stream()
                .filter(p -> p.getQuantity() > 0)
                .map(PositionEntity::getStock)
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet()));
    }

    private OrderEntity ownedOrder(String traderId, UUID id) {
        return orders.findByIdAndTraderId(id, traderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this trader: " + id));
    }

    private void requirePending(OrderEntity order, String action) {
        if (order.getStatus() != OrderStatus.PENDING)
            throw conflict("Only PENDING orders can be " + action + ". Current status: " + order.getStatus());
    }

    private void ensureSectorMatches(PositionEntity p, String sector) {
        if (!p.getSector().equalsIgnoreCase(sector))
            throw conflict("Sector mismatch for " + p.getStock() + ": existing sector is " + p.getSector());
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
