package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.accounts.Account;
import org.poo.main.cards.Card;

import java.util.List;

public class UserHistoryTransactions {
    private User user;

    public UserHistoryTransactions(User user) {
        this.user = user;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addNewAccountTransaction(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newAccountNode = mapper.createObjectNode();
        newAccountNode.put("timestamp", command.getTimestamp());
        newAccountNode.put("description", "New account created");

        user.getTransactionHistory().add(newAccountNode);
        return newAccountNode;
    }

    /**
     *
     * @param command
     * @param card
     */
    public ObjectNode addNewCardTransaction(final CommandInput command, final Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", command.getTimestamp());
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", command.getEmail());
        newCardNode.put("account", command.getAccount());

        user.getTransactionHistory().add(newCardNode);
        return newCardNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addPayOnlineInsufficientFunds(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Insufficient funds");

        user.getTransactionHistory().add(newPayOnlineNode);
        return newPayOnlineNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addSplitPaymentTransaction(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newSplitPaymentNode = mapper.createObjectNode();
        newSplitPaymentNode.put("timestamp", command.getTimestamp());

        String formattedAmount = String.format("%.2f", command.getAmount());
        newSplitPaymentNode.put("description", "Split payment of " + formattedAmount + " "
                + command.getCurrency());
        newSplitPaymentNode.put("currency", command.getCurrency());
        newSplitPaymentNode.put("amount", command.getAmount() / command.getAccounts().size());

        ArrayNode involvedAccounts = mapper.createArrayNode();
        for (String account : command.getAccounts()) {
            involvedAccounts.add(account);
        }

        newSplitPaymentNode.set("involvedAccounts", involvedAccounts);

        user.getTransactionHistory().add(newSplitPaymentNode);
        return newSplitPaymentNode;
    }

    /**
     * @param command
     * @param accounts
     * @param accountIBAN
     */
    public ObjectNode addSplitPaymentError(final CommandInput command, final List<String> accounts,
                                           final String accountIBAN) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newSplitPaymentNode = mapper.createObjectNode();
        newSplitPaymentNode.put("timestamp", command.getTimestamp());

        String formattedAmount = String.format("%.2f", command.getAmount());
        newSplitPaymentNode.put("description", "Split payment of " + formattedAmount + " "
                + command.getCurrency());
        newSplitPaymentNode.put("currency", command.getCurrency());
        newSplitPaymentNode.put("amount", command.getAmount() / command.getAccounts().size());

        ArrayNode involvedAccounts = mapper.createArrayNode();
        for (String account : command.getAccounts()) {
            involvedAccounts.add(account);
        }

        newSplitPaymentNode.set("involvedAccounts", involvedAccounts);
        newSplitPaymentNode.put("error", "Account " + accountIBAN
                + " has insufficient funds for a split payment.");

        user.getTransactionHistory().add(newSplitPaymentNode);
        return newSplitPaymentNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addInterestRateChanged(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newInterestRateNode = mapper.createObjectNode();
        newInterestRateNode.put("timestamp", command.getTimestamp());
        newInterestRateNode.put("description", "Interest rate of the account changed to "
                + command.getInterestRate());

        user.getTransactionHistory().add(newInterestRateNode);
        return newInterestRateNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addSendMoneyInsufficientFunds(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Insufficient funds");

        user.getTransactionHistory().add(newPayOnlineNode);
        return newPayOnlineNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addCardIsFrozenCheck(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newFrozenCardNode = mapper.createObjectNode();
        newFrozenCardNode.put("timestamp", command.getTimestamp());
        newFrozenCardNode.put("description", "You have reached the " +
                "minimum amount of funds, the card will be frozen");

        user.getTransactionHistory().add(newFrozenCardNode);
        return newFrozenCardNode;
    }

    /**
     *
     * @param command
     */
    public ObjectNode addCardIsFrozen(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newFrozenCardNode = mapper.createObjectNode();
        newFrozenCardNode.put("timestamp", command.getTimestamp());
        newFrozenCardNode.put("description", "The card is frozen");

        user.getTransactionHistory().add(newFrozenCardNode);
        return newFrozenCardNode;
    }

    /**
     *
     * @param command
     * @param pay
     */
    public ObjectNode addPayOnlinePayment(final CommandInput command, final double pay) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Card payment");
        newPayOnlineNode.put("amount", pay);
        newPayOnlineNode.put("commerciant", command.getCommerciant());

        user.getTransactionHistory().add(newPayOnlineNode);
        return newPayOnlineNode;
    }

    /**
     *
     * @param command
     * @param sender
     * @param receiver
     */
    public ObjectNode addSendMoneyTransaction(final CommandInput command, final Account sender,
                                              final Account receiver) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newSendMoneyNode = mapper.createObjectNode();
        newSendMoneyNode.put("timestamp", command.getTimestamp());
        newSendMoneyNode.put("description", command.getDescription());
        newSendMoneyNode.put("senderIBAN", sender.getAccountIBAN());
        newSendMoneyNode.put("receiverIBAN", receiver.getAccountIBAN());
        newSendMoneyNode.put("amount", command.getAmount() + " " + sender.getCurrency());
        newSendMoneyNode.put("transferType", "sent");

        user.getTransactionHistory().add(newSendMoneyNode);
        return newSendMoneyNode;
    }

    /**
     *
     * @param command
     * @param sender
     * @param receiver
     */
    public ObjectNode addReceiveMoneyTransaction(final CommandInput command, final Account sender,
                                                 final Account receiver, double rate) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newSendMoneyNode = mapper.createObjectNode();
        newSendMoneyNode.put("timestamp", command.getTimestamp());
        newSendMoneyNode.put("description", command.getDescription());
        newSendMoneyNode.put("senderIBAN", sender.getAccountIBAN());
        newSendMoneyNode.put("receiverIBAN", receiver.getAccountIBAN());
        newSendMoneyNode.put("amount", command.getAmount() / rate + " " + receiver.getCurrency());
        newSendMoneyNode.put("transferType", "received");

        user.getTransactionHistory().add(newSendMoneyNode);
        return newSendMoneyNode;
    }

    /**
     *
     * @param timestamp
     * @param card
     * @param account
     * @param user
     */
    public ObjectNode addNewCardTransaction(final int timestamp, final Card card,
                                            final Account account, final User user) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", timestamp);
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", user.getEmail());
        newCardNode.put("account", account.getAccountIBAN());

        user.getTransactionHistory().add(newCardNode);
        return newCardNode;
    }

    public ObjectNode addBalanceNonZero(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deleteCardNode = mapper.createObjectNode();
        deleteCardNode.put("timestamp", command.getTimestamp());
        deleteCardNode.put("description", "Account couldn't be deleted - there are funds remaining");

        user.getTransactionHistory().add(deleteCardNode);
        return deleteCardNode;
    }

    public ObjectNode addDeleteCardTransaction(final CommandInput command, final User user,
                                               final Account account, final Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deleteCardNode = mapper.createObjectNode();
        deleteCardNode.put("timestamp", command.getTimestamp());
        deleteCardNode.put("description", "The card has been destroyed");
        deleteCardNode.put("card", card.getNumber());
        deleteCardNode.put("cardHolder", user.getEmail());
        deleteCardNode.put("account", account.getAccountIBAN());

        user.getTransactionHistory().add(deleteCardNode);
        return deleteCardNode;
    }
}
