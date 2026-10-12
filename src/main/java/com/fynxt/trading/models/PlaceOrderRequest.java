package com.fynxt.trading.models;

import com.fynxt.trading.enums.OrderSide;
import jakarta.validation.constraints.*;

public record PlaceOrderRequest(@NotBlank @Size(max = 64) String traderId,
                                @NotBlank @Pattern(regexp = "[A-Za-z0-9.]{1,16}") String stock,
                                @NotBlank @Size(max = 32) String sector,
                                @Positive long quantity,
                                @NotNull OrderSide side) {
}
