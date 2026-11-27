package com.fundatech.shareway.paymentsettlement.bdd;

import com.fundatech.shareway.paymentsettlement.application.PaymentAuthorizationService;
import com.fundatech.shareway.paymentsettlement.domain.model.CardBrand;
import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentMethodRepository;
import io.cucumber.java.en.Given;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Steps que preparan datos de pagos directamente sobre los puertos y servicios de la aplicación.
 */
public class PaymentSteps {

    private final PaymentMethodRepository paymentMethods;
    private final PaymentAuthorizationService authorizationService;
    private final ScenarioContext context;

    public PaymentSteps(PaymentMethodRepository paymentMethods, PaymentAuthorizationService authorizationService,
                        ScenarioContext context) {
        this.paymentMethods = paymentMethods;
        this.authorizationService = authorizationService;
        this.context = context;
    }

    @Given("passenger {long} has an active payment method with card token {string} saved as {string}")
    public void passengerHasPaymentMethod(Long passengerId, String cardToken, String name) {
        context.put(name, registerCard(TestIds.uuid(passengerId), cardToken).getId());
    }

    @Given("passenger {long} has an active payment method with card token {string} registered {int} minutes ago saved as {string}")
    public void passengerHasPaymentMethodRegisteredAt(Long passengerId, String cardToken, int minutesAgo,
                                                      String name) {
        PaymentMethod method = PaymentMethod.register(TestIds.uuid(passengerId), cardToken, CardBrand.VISA, "4242",
                12, 2099, YearMonth.now());
        ReflectionTestUtils.setField(method, "createdAt", Instant.now().minus(Duration.ofMinutes(minutesAgo)));
        context.put(name, paymentMethods.save(method).getId());
    }

    @Given("passenger {long} has an inactive payment method with card token {string} saved as {string}")
    public void passengerHasInactivePaymentMethod(Long passengerId, String cardToken, String name) {
        PaymentMethod method = registerCard(TestIds.uuid(passengerId), cardToken);
        method.deactivate();
        context.put(name, paymentMethods.save(method).getId());
    }

    @Given("booking {long} has an authorized payment of {string} PEN for passenger {long}")
    public void bookingHasAuthorizedPayment(Long bookingId, String amount, Long passengerId) {
        UUID passenger = TestIds.uuid(passengerId);
        PaymentMethod method = registerCard(passenger, "tok_booking_" + bookingId);
        authorizationService.authorize(TestIds.uuid(bookingId), passenger, method.getId(), new BigDecimal(amount),
                "PEN");
    }

    private PaymentMethod registerCard(UUID passengerId, String cardToken) {
        return paymentMethods.save(PaymentMethod.register(passengerId, cardToken, CardBrand.VISA, "4242", 12, 2099,
                YearMonth.now()));
    }
}
