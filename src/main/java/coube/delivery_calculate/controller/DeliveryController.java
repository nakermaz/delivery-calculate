package coube.delivery_calculate.controller;

import coube.delivery_calculate.model.dto.CalculateRequestDto;
import coube.delivery_calculate.model.dto.CalculateResponseDto;
import coube.delivery_calculate.model.enums.CurrencyType;
import coube.delivery_calculate.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/calculate")
    public ResponseEntity<CalculateResponseDto> calculate(
            @RequestHeader(defaultValue = "KZT")
            CurrencyType currencyType,
            @Valid @RequestBody
            CalculateRequestDto calculateRequestDto
    ) {
        return ResponseEntity.ok(deliveryService.calculate(calculateRequestDto, currencyType));
    }
}
