package org.poo.main.accounts.accountTypes;

import org.poo.main.accounts.Account;

public class ClassicAccount extends Account {
    public ClassicAccount(final String currency) {
        super(currency);
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "classic";
    }
}
