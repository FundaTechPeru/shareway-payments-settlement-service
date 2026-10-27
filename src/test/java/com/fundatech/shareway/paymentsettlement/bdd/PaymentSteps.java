package com.fundatech.shareway.paymentsettlement.bdd;

import com.fundatech.shareway.paymentsettlement.application.PaymentAuthorizationService;
import com.fundatech.shareway.paymentsettlement.domain.model.CardBrand;
import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentMethodRepository;
import io.cucumber.java.en.Given;
import java.math.BigDecimal;
import java.time.YearMonth;

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
        context.put(name, registerCard(passengerId, cardToken).getId());
    }

    @Given("booking {long} has an authorized payment of {string} PEN for passenger {long}")
    public void bookingHasAuthorizedPayment(Long bookingId, String amount, Long passengerId) {
        PaymentMethod method = registerCard(passengerId, "tok_booking_" + bookingId);
        authorizationService.authorize(bookingId, passengerId, method.getId(), new BigDecimal(amount), "PEN");
    }

    private PaymentMethod registerCard(Long passengerId, String cardToken) {
        return paymentMethods.save(PaymentMethod.register(passengerId, cardToken, CardBrand.VISA, "4242", 12, 2099,
                YearMonth.now()));
    }
}
