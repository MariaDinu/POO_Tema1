package org.poo.main.coreBankingSystemComponents.accounts.accountFactory;

import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.coreBankingSystemComponents.accounts.accountTypes.ClassicAccount;
import org.poo.main.coreBankingSystemComponents.accounts.accountTypes.SavingsAccount;

public final class AccountFactory {
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
