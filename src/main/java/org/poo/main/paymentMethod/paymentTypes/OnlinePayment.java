package org.poo.main.paymentMethod.paymentTypes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.coreBankingSystemComponents.User;
import org.poo.main.transactions.UserHistoryTransactions;
import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.coreBankingSystemComponents.cards.Card;
import org.poo.main.paymentMethod.PaymentStrategy;

public class OnlinePayment implements PaymentStrategy {

    private CommandInput command;
    private ObjectNode objectNode;
    private ArrayNode output;

    public OnlinePayment(final CommandInput command, final ObjectNode objectNode,
                         final ArrayNode output) {
        this.command = command;
        this.objectNode = objectNode;
        this.output = output;
    }

    /**
     *
     * @param bankingSystem
     */
    @Override
    public void pay(final BankingSystem bankingSystem) {
        Card card = bankingSystem.findCard(command.getCardNumber());

        double rate = bankingSystem.getExchangeRate(command.getCurrency(),
                bankingSystem.findAccountOfCard(command.getCardNumber()).getCurrency());

        double pay = command.getAmount() * rate;

        if (bankingSystem.findAccountOfCard(command.getCardNumber()).getBalance() < pay) {
            Account account = bankingSystem.findAccountOfCard(card.getNumber());
            User user = bankingSystem.findUserOfAccountOfCard(card.getNumber());

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            account.getTransactionHistory().add(userHistory.addPayOnlineInsufficientFunds(command));
        } else {
            Account account = bankingSystem.findAccountOfCard(command.getCardNumber());

            account.setBalance(account.getBalance() - pay);

            User user = bankingSystem.findUserOfAccountOfCard(card.getNumber());

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            account.getTransactionHistory().add(userHistory.addPayOnlinePayment(command, pay));

            card.setHasPayed(true);
            if (card.getHasPayed()) {
                bankingSystem.deleteOneTimeCard(command, card);
                bankingSystem.createCard("OneTimeCard", command.getTimestamp(), account);
            }
        }
    }
}
