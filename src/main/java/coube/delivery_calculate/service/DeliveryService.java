package coube.delivery_calculate.service;

import coube.delivery_calculate.model.dto.CalculateRequestDto;
import coube.delivery_calculate.model.dto.CalculateResponseDto;
import coube.delivery_calculate.model.enums.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private static final BigDecimal BASE_PRICE_KZT = new BigDecimal("8");
    private static final BigDecimal BASE_PRICE_RUB = new BigDecimal("0.17");
    private static final BigDecimal BASE_PRICE_USD = new BigDecimal("0.0021");
    private static final BigDecimal URGENT_SURCHARGE_PERCENT = new BigDecimal("20");
    private static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");

    public CalculateResponseDto calculate(CalculateRequestDto requestDto, CurrencyType currencyType) {
        BigDecimal basePrice = BigDecimal.valueOf(requestDto.distanceKm())
                .multiply(BigDecimal.valueOf(requestDto.weightTon()))
                .multiply(getBasePrice(currencyType));

        BigDecimal urgentSurcharge = BigDecimal.ZERO;
        if (requestDto.isUrgent()) {
            urgentSurcharge = basePrice
                    .multiply(URGENT_SURCHARGE_PERCENT)
                    .divide(PERCENT_DIVISOR, RoundingMode.HALF_UP);
        }

        BigDecimal cargoTypePrecent = BigDecimal.valueOf(requestDto.cargoType().getPrecent());
        BigDecimal cargoTypeSurcharge = basePrice
                .multiply(cargoTypePrecent)
                .divide(PERCENT_DIVISOR, RoundingMode.HALF_UP);

        BigDecimal totalPrice = basePrice
                .add(urgentSurcharge)
                .add(cargoTypeSurcharge);

        return new CalculateResponseDto(
                basePrice,
                urgentSurcharge,
                cargoTypeSurcharge,
                totalPrice,
                currencyType
        );
    }

    private BigDecimal getBasePrice(CurrencyType currencyType) {
        return switch (currencyType) {
            case KZT -> BASE_PRICE_KZT;
            case RUB -> BASE_PRICE_RUB;
            case USD -> BASE_PRICE_USD;
        };
    }
}
