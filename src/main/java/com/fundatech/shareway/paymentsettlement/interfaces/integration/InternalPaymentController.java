package com.fundatech.shareway.paymentsettlement.interfaces.integration;

import com.fundatech.shareway.paymentsettlement.application.IdempotentResult;
import com.fundatech.shareway.paymentsettlement.application.PaymentAuthorizationService;
import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import com.fundatech.shareway.paymentsettlement.infrastructure.config.OpenApiConfig;
import com.fundatech.shareway.paymentsettlement.interfaces.rest.dto.AuthorizePaymentRequest;
import com.fundatech.shareway.paymentsettlement.interfaces.rest.dto.PaymentResponse;
import com.fundatech.shareway.paymentsettlement.shared.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * Endpoints internos de pagos invocados por otros microservicios con API key.
 */
@RestController
@RequestMapping("/api/v1/internal/payments")
@Tag(name = "Internal Payments", description = "Service-to-service payment operations")
@SecurityRequirement(name = OpenApiConfig.API_KEY_SCHEME)
public class InternalPaymentController {

    private final PaymentAuthorizationService authorizationService;

    public InternalPaymentController(PaymentAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/authorizations")
    @Operation(summary = "Authorize the payment of a booking (201 new, 200 idempotent replay)",
            description = "Idempotent by bookingId. paymentMethodId is optional: when omitted, the passenger's most "
                    + "recently registered active payment method is used.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment authorized"),
            @ApiResponse(responseCode = "200", description = "Booking already had an authorized or captured payment"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-Internal-Api-Key",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "402", description = "Payment declined by the gateway",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "paymentMethodId not found, inactive or owned by another passenger",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422",
                    description = "paymentMethodId omitted and the passenger has no active payment method",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "503", description = "Payment gateway unavailable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<PaymentResponse> authorize(@Valid @RequestBody AuthorizePaymentRequest request) {
        IdempotentResult<Payment> result = authorizationService.authorize(request.bookingId(),
                request.passengerId(), request.paymentMethodId(), request.amount(), request.currency());
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(PaymentResponse.from(result.value()));
    }
}
