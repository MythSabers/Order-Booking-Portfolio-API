package com.fynxt.trading.models;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {
}
