package coube.delivery_calculate.model.dto;

import coube.delivery_calculate.model.enums.CurrencyType;

import java.math.BigDecimal;

public record CalculateResponseDto (
        BigDecimal basePrice,
        BigDecimal urgentSurcharge,
        BigDecimal cargoTypeSurcharge,
        BigDecimal totalPrice,
        CurrencyType currency
) {
}
