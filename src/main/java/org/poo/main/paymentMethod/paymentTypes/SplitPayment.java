package org.poo.main.paymentMethod.paymentTypes;

import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.coreBankingSystemComponents.User;
import org.poo.main.transactions.UserHistoryTransactions;
import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.paymentMethod.PaymentStrategy;

public class SplitPayment implements PaymentStrategy {

    private CommandInput command;

    public SplitPayment(final CommandInput command) {
        this.command = command;
    }

    /**
     * Executes the split payment using the banking system instance to update objects.
     *
     * This method splits the total payment amount equally among the specified accounts,
     * checks each account's balance, and processes the payment if all accounts can pay
     * their share. If any account has insufficient funds, an error is logged for each
     * account involved.
     *
     * @param bankingSystem the banking system instance to execute the payment and update objects.
     */
    @Override
    public void pay(final BankingSystem bankingSystem) {
        double amount = command.getAmount() / command.getAccounts().size();
        boolean canAllAccountsPay = true;
        String accountCantPay = "";

        for (String accountIBAN : command.getAccounts()) {
            Account account = bankingSystem.findAccount(accountIBAN);

            double rate = bankingSystem.getExchangeRate(command.getCurrency(),
                    account.getCurrency());
            double pay = amount * rate;

            if (account.getBalance() < pay) {
                canAllAccountsPay = false;
                accountCantPay = account.getAccountIBAN();
            }
        }

        if (canAllAccountsPay) {
            for (String accountIBAN : command.getAccounts()) {
                Account account = bankingSystem.findAccount(accountIBAN);

                double rate = bankingSystem.getExchangeRate(command.getCurrency(),
                        account.getCurrency());
                double pay = amount * rate;

                account.setBalance(account.getBalance() - pay);

                User user = bankingSystem.findUserOfAccount(account.getAccountIBAN());

                UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
                account.getTransactionHistory()
                        .add(userHistory.addSplitPaymentTransaction(command));
            }
        } else {
            for (String accountIBAN : command.getAccounts()) {
                Account account = bankingSystem.findAccount(accountIBAN);

                User user = bankingSystem.findUserOfAccount(accountIBAN);

                UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
                account.getTransactionHistory()
                        .add(userHistory.addSplitPaymentError(command,
                                command.getAccounts(), accountCantPay));
            }
        }
    }
}
