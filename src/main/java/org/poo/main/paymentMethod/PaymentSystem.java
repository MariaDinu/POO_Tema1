package org.poo.main.paymentMethod;

import org.poo.main.BankingSystem;

public class PaymentSystem {

    private BankingSystem bankingSystem;
    private PaymentStrategy paymentStrategy;

    public PaymentSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    public BankingSystem getBankingSystem() {
        return bankingSystem;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void makePayment() {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy is not set!");
        }
        paymentStrategy.pay(bankingSystem);
    }
}
