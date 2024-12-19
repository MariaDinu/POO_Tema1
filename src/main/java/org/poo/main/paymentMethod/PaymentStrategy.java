package org.poo.main.paymentMethod;

import org.poo.main.coreBankingSystemComponents.BankingSystem;

public interface PaymentStrategy {
    /**
     * Executes the payment process: card, transfer or split.
     *
     * @param bankingSystem the banking system instance to perform the payment
     *                      and to update objects.
     */
    void pay(BankingSystem bankingSystem);
}
