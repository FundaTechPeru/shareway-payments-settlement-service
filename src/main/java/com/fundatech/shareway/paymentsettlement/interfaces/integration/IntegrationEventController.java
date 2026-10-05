package com.fundatech.shareway.paymentsettlement.interfaces.integration;

import com.fundatech.shareway.paymentsettlement.application.IdempotentResult;
import com.fundatech.shareway.paymentsettlement.application.PaymentCaptureService;
import com.fundatech.shareway.paymentsettlement.application.PaymentCaptureService.TripCharge;
import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import com.fundatech.shareway.paymentsettlement.infrastructure.config.OpenApiConfig;
import com.fundatech.shareway.paymentsettlement.interfaces.rest.dto.SettlementResponse;
import com.fundatech.shareway.paymentsettlement.interfaces.rest.dto.TripCompletedRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recepción de los Domain Events de otros servicios mientras no exista un broker de mensajes.
 */
@RestController
@RequestMapping("/api/v1/internal/events")
@Tag(name = "Integration Events", description = "Domain events received from other services")
@SecurityRequirement(name = OpenApiConfig.API_KEY_SCHEME)
public class IntegrationEventController {

    private final PaymentCaptureService captureService;

    public IntegrationEventController(PaymentCaptureService captureService) {
        this.captureService = captureService;
    }

    @PostMapping("/trip-completed")
    @Operation(summary = "TripCompleted: capture the trip bookings and create the driver settlement")
    public ResponseEntity<SettlementResponse> tripCompleted(@Valid @RequestBody TripCompletedRequest request) {
        IdempotentResult<DriverSettlement> result = captureService.captureTrip(request.tripId(), request.driverId(),
                request.completedAt(), request.bookings().stream()
                        .map(charge -> new TripCharge(charge.bookingId(), charge.finalAmount()))
                        .toList());
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(SettlementResponse.from(result.value()));
    }
}
