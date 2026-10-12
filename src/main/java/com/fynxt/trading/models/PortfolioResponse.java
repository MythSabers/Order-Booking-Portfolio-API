package com.fynxt.trading.models;

import java.util.Map;

public record PortfolioResponse(String traderId, Map<String, Long> positions, Map<String, Long> sectorBreakdown) {
}

