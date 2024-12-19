package org.poo.main.paymentMethod;

import org.poo.main.coreBankingSystemComponents.BankingSystem;

public class PaymentSystem {

    private BankingSystem bankingSystem;
    private PaymentStrategy paymentStrategy;

    public PaymentSystem(final BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    /**
     *
     * @return
     */
    public BankingSystem getBankingSystem() {
        return bankingSystem;
    }

    /**
     *
     * @param paymentStrategy
     */
    public void setPaymentStrategy(final PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /**
     *
     */
    public void makePayment() {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy is not set!");
        }
        paymentStrategy.pay(bankingSystem);
    }
}
