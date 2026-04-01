package coube.delivery_calculate.service;

import coube.delivery_calculate.model.dto.CalculateRequestDto;
import coube.delivery_calculate.model.dto.CalculateResponseDto;
import coube.delivery_calculate.model.enums.CargoType;
import coube.delivery_calculate.model.enums.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService();
    }

    @Test
    void calculate_standardCargoNotUrgent_returnsBasePriceOnly() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.STANDARD, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(new BigDecimal("8000.0"), result.basePrice());
        assertEquals(BigDecimal.ZERO, result.urgentSurcharge());
        assertEquals(new BigDecimal("0.0"), result.cargoTypeSurcharge());
        assertEquals(new BigDecimal("8000.0"), result.totalPrice());
        assertEquals(CurrencyType.KZT, result.currency());
    }

    @Test
    void calculate_standardCargoUrgent_addsUrgentSurcharge() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.STANDARD, true);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(new BigDecimal("8000.0"), result.basePrice());
        assertEquals(new BigDecimal("1600.0"), result.urgentSurcharge());
        assertEquals(new BigDecimal("0.0"), result.cargoTypeSurcharge());
        assertEquals(new BigDecimal("9600.0"), result.totalPrice());
    }

    @Test
    void calculate_fragileCargoNotUrgent_addsCargoSurcharge() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.FRAGILE, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(new BigDecimal("8000.0"), result.basePrice());
        assertEquals(BigDecimal.ZERO, result.urgentSurcharge());
        assertEquals(new BigDecimal("800.0"), result.cargoTypeSurcharge());
        assertEquals(new BigDecimal("8800.0"), result.totalPrice());
    }

    @Test
    void calculate_oversizedCargoUrgent_addsBothSurcharges() {
        var request = new CalculateRequestDto(200L, 5.0, CargoType.OVERSIZED, true);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(new BigDecimal("8000.0"), result.basePrice());
        assertEquals(new BigDecimal("1600.0"), result.urgentSurcharge());
        assertEquals(new BigDecimal("2000.0"), result.cargoTypeSurcharge());
        assertEquals(new BigDecimal("11600.0"), result.totalPrice());
    }

    @Test
    void calculate_rubCurrency_usesRubBasePrice() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.STANDARD, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.RUB);

        assertEquals(0, new BigDecimal("170.0").compareTo(result.basePrice()));
        assertEquals(CurrencyType.RUB, result.currency());
    }

    @Test
    void calculate_usdCurrency_usesUsdBasePrice() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.STANDARD, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.USD);

        assertEquals(0, new BigDecimal("2.1").compareTo(result.basePrice()));
        assertEquals(CurrencyType.USD, result.currency());
    }

    @ParameterizedTest
    @EnumSource(CurrencyType.class)
    void calculate_allCurrencies_returnCorrectCurrency(CurrencyType currency) {
        var request = new CalculateRequestDto(50L, 1.0, CargoType.STANDARD, false);

        CalculateResponseDto result = deliveryService.calculate(request, currency);

        assertEquals(currency, result.currency());
        assertTrue(result.basePrice().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(result.basePrice(), result.totalPrice()); // STANDARD, not urgent
    }

    @Test
    void calculate_minValues_calculatesCorrectly() {
        var request = new CalculateRequestDto(1L, 0.1, CargoType.STANDARD, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(0, new BigDecimal("0.8").compareTo(result.basePrice()));
    }

    @Test
    void calculate_maxValues_calculatesCorrectly() {
        var request = new CalculateRequestDto(5000L, 120.0, CargoType.OVERSIZED, true);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        BigDecimal expectedBase = new BigDecimal("4800000.0");
        BigDecimal expectedUrgent = expectedBase.multiply(new BigDecimal("20"))
                .divide(new BigDecimal("100"), RoundingMode.HALF_UP);
        BigDecimal expectedCargo = expectedBase.multiply(new BigDecimal("25"))
                .divide(new BigDecimal("100"), RoundingMode.HALF_UP);

        assertEquals(0, expectedBase.compareTo(result.basePrice()));
        assertEquals(0, expectedUrgent.compareTo(result.urgentSurcharge()));
        assertEquals(0, expectedCargo.compareTo(result.cargoTypeSurcharge()));
        assertEquals(0, expectedBase.add(expectedUrgent).add(expectedCargo).compareTo(result.totalPrice()));
    }

    @Test
    void calculate_notUrgent_urgentSurchargeIsZero() {
        var request = new CalculateRequestDto(100L, 10.0, CargoType.FRAGILE, false);

        CalculateResponseDto result = deliveryService.calculate(request, CurrencyType.KZT);

        assertEquals(BigDecimal.ZERO, result.urgentSurcharge());
    }
}
