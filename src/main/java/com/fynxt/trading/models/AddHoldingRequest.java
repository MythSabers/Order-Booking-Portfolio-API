package com.fynxt.trading.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddHoldingRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9.]{1,16}") String stock,
                                @NotBlank @Size(max = 32) String sector,
                                @Positive long quantity) {
}
