package com.fynxt.trading.models;

import java.util.List;

public record OverlapResponse(List<BasketOverlap> overlaps, String dominantBasket, String riskFlag) {
}
