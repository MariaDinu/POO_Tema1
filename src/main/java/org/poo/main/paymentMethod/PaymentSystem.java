package org.poo.main.paymentMethod;

import org.poo.main.coreBankingSystemComponents.BankingSystem;

public class PaymentSystem {

    private BankingSystem bankingSystem;
    private PaymentStrategy paymentStrategy;

    public PaymentSystem(final BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }


    /**
     * Sets the payment strategy to be used for processing payments.
     *
     * @param paymentStrategy the specific payment strategy implementation: OnlinePayment,
     *                        TransferPayment or SplitPayment.
     */
    public void setPaymentStrategy(final PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /**
     * Executes a payment using the specified payment strategy.
     *
     * @throws IllegalStateException if the payment strategy is not set.
     */
    public void makePayment() {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy is not set!");
        }
        paymentStrategy.pay(bankingSystem);
    }
}
