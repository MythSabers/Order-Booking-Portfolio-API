package com.fynxt.trading;

import com.fynxt.trading.models.OverlapResponse;
import com.fynxt.trading.utils.OverlapAnalyzer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OverlapAnalyzerTest {
    @Test void workedExampleProducesExpectedPercentagesAndHighRisk() {
        OverlapResponse result= OverlapAnalyzer.analyze(List.of("AAPL","TSLA","NVDA"));
        assertEquals("TECH_HEAVY",result.dominantBasket());
        assertEquals("HIGH",result.riskFlag());
        assertEquals("75.00%",result.overlaps().stream().filter(x->x.basket().equals("TECH_HEAVY")).findFirst().orElseThrow().overlap());
        assertEquals("0.00%",result.overlaps().stream().filter(x->x.basket().equals("FINANCE_HEAVY")).findFirst().orElseThrow().overlap());
        assertEquals("50.00%",result.overlaps().stream().filter(x->x.basket().equals("BALANCED")).findFirst().orElseThrow().overlap());
    }
    @Test void ignoresDuplicateAndCaseDifferences() {
        OverlapResponse result=OverlapAnalyzer.analyze(List.of("aapl","AAPL","tsla"));
        assertEquals("57.14%",result.overlaps().stream().filter(x->x.basket().equals("TECH_HEAVY")).findFirst().orElseThrow().overlap());
        assertEquals("MEDIUM",result.riskFlag());
    }
    @Test void emptyPortfolioIsLowRisk() {
        OverlapResponse result=OverlapAnalyzer.analyze(List.of());
        assertEquals("LOW",result.riskFlag());
        assertEquals("0.00%",result.overlaps().get(0).overlap());
    }
}
