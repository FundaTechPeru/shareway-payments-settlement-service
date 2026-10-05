package com.fundatech.shareway.paymentsettlement.application;

import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentRepository;
import com.fundatech.shareway.paymentsettlement.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultas de solo lectura sobre los pagos de un pasajero.
 */
@Service
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository payments;

    public PaymentQueryService(PaymentRepository payments) {
        this.payments = payments;
    }

    public List<Payment> listPassengerPayments(Long passengerId) {
        return payments.findByPassengerId(passengerId);
    }

    /**
     * Devuelve un pago del pasajero; uno ajeno se trata como inexistente.
     */
    public Payment getPassengerPayment(Long passengerId, Long paymentId) {
        return payments.findByIdAndPassengerId(paymentId, passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}
