package com.fundatech.shareway.paymentsettlement.bdd;

import com.fundatech.shareway.paymentsettlement.domain.model.CardBrand;
import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentMethodRepository;
import io.cucumber.java.en.Given;
import java.time.YearMonth;

/**
 * Steps que preparan datos de pagos directamente sobre los puertos del dominio.
 */
public class PaymentSteps {

    private final PaymentMethodRepository paymentMethods;
    private final ScenarioContext context;

    public PaymentSteps(PaymentMethodRepository paymentMethods, ScenarioContext context) {
        this.paymentMethods = paymentMethods;
        this.context = context;
    }

    @Given("passenger {long} has an active payment method with card token {string} saved as {string}")
    public void passengerHasPaymentMethod(Long passengerId, String cardToken, String name) {
        PaymentMethod method = paymentMethods.save(PaymentMethod.register(passengerId, cardToken, CardBrand.VISA,
                "4242", 12, 2099, YearMonth.now()));
        context.put(name, method.getId());
    }
}
