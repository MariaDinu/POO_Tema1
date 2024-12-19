package org.poo.main.paymentMethod;

import org.poo.main.BankingSystem;

public interface PaymentStrategy {
    void pay(BankingSystem bankingSystem);
}
