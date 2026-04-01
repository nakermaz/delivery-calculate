package coube.delivery_calculate.model.dto;

import coube.delivery_calculate.model.enums.CargoType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CalculateRequestDto (
        @Min(value = 1)
        @Max(value = 5000)
        Long distanceKm,
        @DecimalMin(value = "0.1")
        @DecimalMax(value = "120")
        Double weightTon,
        CargoType cargoType,
        Boolean isUrgent
) {
}
