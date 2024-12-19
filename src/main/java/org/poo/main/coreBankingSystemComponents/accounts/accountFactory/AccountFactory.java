package org.poo.main.coreBankingSystemComponents.accounts.accountFactory;

import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.coreBankingSystemComponents.accounts.accountTypes.ClassicAccount;
import org.poo.main.coreBankingSystemComponents.accounts.accountTypes.SavingsAccount;

public final class AccountFactory {
    /**
     * Creates an account of the specified type with the given parameters.
     *
     * @param type the type of account to create.
     * @param currency the currency in which the account will operate.
     * @param interestRate the interest rate for the account (only savings accounts).
     * @return an instance of the created account.
     * @throws IllegalArgumentException if the provided account type is not recognized.
     */
    public static Account createAccount(final String type, final String currency,
                                        final double interestRate) {
        switch (type) {
            case "classic": return new ClassicAccount(currency);
            case "savings": return new SavingsAccount(currency, interestRate);

            default:
                throw new IllegalArgumentException("The account type " + type
                        + " is not recognized.");
        }
    }

    private AccountFactory() { }
}
