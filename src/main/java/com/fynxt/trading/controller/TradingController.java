package com.fynxt.trading.controller;

import com.fynxt.trading.models.*;
import com.fynxt.trading.services.TradingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TradingController {
    private final TradingService service;

    // 1. place an order
    @PostMapping("/orders")
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return service.placeOrder(request);
    }

    // 2. Fill an order
    @PostMapping("/traders/{traderId}/orders/{orderId}/fill")
    public OrderResponse fill(@PathVariable @NotBlank String traderId, @PathVariable UUID orderId) {
        return service.fill(traderId, orderId);
    }

    // 3. Cancel an order
    @PostMapping("/traders/{traderId}/orders/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable @NotBlank String traderId, @PathVariable UUID orderId) {
        return service.cancel(traderId, orderId);
    }

    // 4. Get Portfolio data for trader
    @GetMapping("/traders/{traderId}/portfolio")
    public PortfolioResponse portfolio(@PathVariable @NotBlank String traderId) {
        return service.portfolio(traderId);
    }

    // 5. Sector Overlap Analysis
    @GetMapping("/traders/{traderId}/sector-overlap")
    public OverlapResponse overlap(@PathVariable @NotBlank String traderId) {
        return service.overlap(traderId);
    }

    // 6. Add Directly to Portfolio
    @PostMapping("/traders/{traderId}/portfolio/holdings")
    public PortfolioResponse addHolding(@PathVariable @NotBlank String traderId,
                                        @Valid @RequestBody AddHoldingRequest request) {
        return service.addHolding(traderId, request);
    }
}
