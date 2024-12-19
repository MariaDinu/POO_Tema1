package org.poo.main.paymentMethod;

import org.poo.main.coreBankingSystemComponents.BankingSystem;

public interface PaymentStrategy {
    /**
     *
     * @param bankingSystem
     */
    void pay(BankingSystem bankingSystem);
}
