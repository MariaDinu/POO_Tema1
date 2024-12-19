package org.poo.main.accounts.accountTypes;

import org.poo.main.accounts.Account;

public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final String currency, final double interestRate) {
        super(currency);
        this.interestRate = interestRate;
    }

    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     *
     */
    public void addInterest() {
        super.setBalance(super.getBalance() * interestRate);
    }

    public double getInterestRate() {
        return interestRate;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "savings";
    }
}
