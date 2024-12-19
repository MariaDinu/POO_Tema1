package org.poo.main.accounts.accountFactory;

import org.poo.main.accounts.Account;
import org.poo.main.accounts.accountTypes.ClassicAccount;
import org.poo.main.accounts.accountTypes.SavingsAccount;

public class AccountFactory {
    /**
     *
     * @param type
     * @return
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
