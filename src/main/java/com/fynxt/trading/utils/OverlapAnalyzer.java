package com.fynxt.trading.utils;

import com.fynxt.trading.models.BasketOverlap;
import com.fynxt.trading.models.OverlapResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public final class OverlapAnalyzer {
    private static final Map<String, Set<String>> BASKETS = Map.of(
            "TECH_HEAVY", Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA"),
            "FINANCE_HEAVY", Set.of("JPM", "GS", "BAC", "MS", "WFC"),
            "BALANCED", Set.of("AAPL", "JPM", "XOM", "JNJ", "TSLA")
    );

    private OverlapAnalyzer() {
    }

    public static OverlapResponse analyze(Collection<String> heldStocks) {
        Set<String> portfolio = new HashSet<>();
        if (heldStocks != null)
            portfolio = heldStocks.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim().toUpperCase(Locale.ROOT))
                    .collect(Collectors.toSet());

        List<BasketOverlap> results = new ArrayList<>();
        String dominant = null;
        BigDecimal max = BigDecimal.valueOf(-1);
        for (var basket : BASKETS.entrySet()) {
            long common = portfolio.stream().filter(basket.getValue()::contains).count();
            int denominator = portfolio.size() + basket.getValue().size();
            BigDecimal percent = denominator == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(2L * common).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
            results.add(new BasketOverlap(basket.getKey(), percent.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%"));
            if (percent.compareTo(max) > 0) {
                max = percent;
                dominant = basket.getKey();
            }
        }
        String risk = max.compareTo(BigDecimal.valueOf(60)) >= 0
                ? "HIGH"
                : max.compareTo(BigDecimal.valueOf(40)) >= 0 ? "MEDIUM" : "LOW";
        return new OverlapResponse(List.copyOf(results), dominant, risk);
    }
}
