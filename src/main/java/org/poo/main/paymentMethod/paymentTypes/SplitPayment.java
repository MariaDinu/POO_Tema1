package org.poo.main.paymentMethod.paymentTypes;

import org.poo.fileio.CommandInput;
import org.poo.main.BankingSystem;
import org.poo.main.User;
import org.poo.main.accounts.Account;
import org.poo.main.paymentMethod.PaymentStrategy;

public class SplitPayment implements PaymentStrategy {

    private CommandInput command;

    public SplitPayment(final CommandInput command) {
        this.command = command;
    }

    @Override
    public void pay(BankingSystem bankingSystem) {
        double amount = command.getAmount() / command.getAccounts().size();
        boolean canAllAccountsPay = true;
        String accountCantPay = "";

        for (String accountIBAN : command.getAccounts()) {
            Account account = bankingSystem.findAccount(accountIBAN);

            double rate = bankingSystem.getExchangeRate(command.getCurrency(), account.getCurrency());
            double pay = amount * rate;

            if (account.getBalance() < pay) {
                canAllAccountsPay = false;
                accountCantPay = account.getAccountIBAN();
            }
        }

        if (canAllAccountsPay) {
            for (String accountIBAN : command.getAccounts()) {
                Account account = bankingSystem.findAccount(accountIBAN);

                double rate = bankingSystem.getExchangeRate(command.getCurrency(), account.getCurrency());
                double pay = amount * rate;

                account.setBalance(account.getBalance() - pay);

                User user = bankingSystem.findUserOfAccount(account.getAccountIBAN());
                account.getTransactionHistory().add(user.addSplitPaymentTransaction(command));
            }
        } else {
            for (String accountIBAN : command.getAccounts()) {
                Account account = bankingSystem.findAccount(accountIBAN);

                User user = bankingSystem.findUserOfAccount(accountIBAN);
                account.getTransactionHistory().add(user.addSplitPaymentError(command, command.getAccounts(), accountCantPay));
            }
        }
    }
}
