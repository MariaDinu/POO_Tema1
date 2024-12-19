package org.poo.main.paymentMethod.paymentTypes;

import org.poo.fileio.CommandInput;
import org.poo.main.BankingSystem;
import org.poo.main.User;
import org.poo.main.UserHistoryTransactions;
import org.poo.main.accounts.Account;
import org.poo.main.paymentMethod.PaymentStrategy;

public class TransferPayment implements PaymentStrategy {

    private CommandInput command;

    public TransferPayment(final CommandInput command) {
        this.command = command;
    }

    @Override
    public void pay(BankingSystem bankingSystem) {
        Account sender = bankingSystem.findAccount(command.getAccount());
        Account receiver = bankingSystem.findAccount(command.getReceiver());

        double rate = bankingSystem.getExchangeRate(sender.getCurrency(), receiver.getCurrency());
        double pay = command.getAmount() * rate;

        double receiverRate = bankingSystem.getExchangeRate(receiver.getCurrency(), sender.getCurrency());

        if (sender.getBalance() < command.getAmount()) {
            User user = bankingSystem.findUserOfAccount(sender.getAccountIBAN());

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            sender.getTransactionHistory().add(userHistory.addSendMoneyInsufficientFunds(command));
        } else {
            sender.setBalance(sender.getBalance() - command.getAmount());
            receiver.setBalance(receiver.getBalance() + pay);

            User user = bankingSystem.findUserOfAccount(sender.getAccountIBAN());

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            sender.getTransactionHistory().add(userHistory.addSendMoneyTransaction(command, sender, receiver));
            user = bankingSystem.findUserOfAccount(receiver.getAccountIBAN());

            userHistory = new UserHistoryTransactions(user);
            receiver.getTransactionHistory().add(userHistory.addReceiveMoneyTransaction(command, sender, receiver, receiverRate));
        }
    }
}
