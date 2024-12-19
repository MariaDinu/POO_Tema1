package org.poo.main.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.User;
import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.coreBankingSystemComponents.cards.Card;

import java.util.List;

public class UserHistoryTransactions {
    private User user;

    public UserHistoryTransactions(final User user) {
        this.user = user;
    }

    /**
     * Logs a new account creation transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs a new card creation transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param card the card associated with the transaction.
     * @return an objectNode representing the transaction.
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
     * Logs an online payment failure due to insufficient funds into the user's transaction
     * history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs a split payment transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs an error for a failed split payment transaction due to insufficient funds into the
     * user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param accounts the list of accounts involved in the transaction.
     * @param accountIBAN the account that failed the transaction.
     * @return an objectNode representing the error transaction.
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
     * Logs an interest rate change transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs a failed money transfer transaction due to insufficient funds into the
     * user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs a card frozen check transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
     */
    public ObjectNode addCardIsFrozenCheck(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newFrozenCardNode = mapper.createObjectNode();
        newFrozenCardNode.put("timestamp", command.getTimestamp());
        newFrozenCardNode.put("description", "You have reached the "
                + "minimum amount of funds, the card will be frozen");

        user.getTransactionHistory().add(newFrozenCardNode);
        return newFrozenCardNode;
    }

    /**
     * Logs a card frozen transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
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
     * Logs an online payment transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param pay the amount paid.
     * @return an objectNode representing the transaction.
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
     * Logs a money transfer transaction for the sender into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param sender the sender's account.
     * @param receiver the receiver's account.
     * @return an objectNode representing the transaction.
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
     * Logs a money transfer transaction for the receiver into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param sender  the sender's account.
     * @param receiver the receiver's account.
     * @param rate the exchange rate applied to the transaction.
     * @return an objectNode representing the transaction.
     */
    public ObjectNode addReceiveMoneyTransaction(final CommandInput command, final Account sender,
                                                 final Account receiver, final double rate) {
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
     * Logs a new card creation transaction into the user's transaction history.
     *
     * @param timestamp the timestamp of the transaction.
     * @param card the card created.
     * @param account the account associated with the card.
     * @param sentUser the user owning the card.
     * @return an objectNode representing the transaction.
     */
    public ObjectNode addNewCardTransaction(final int timestamp, final Card card,
                                            final Account account, final User sentUser) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", timestamp);
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", sentUser.getEmail());
        newCardNode.put("account", account.getAccountIBAN());

        sentUser.getTransactionHistory().add(newCardNode);
        return newCardNode;
    }

    /**
     * Logs an error indicating that an account with a non-zero balance could not be deleted
     * into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @return an objectNode representing the transaction.
     */
    public ObjectNode addBalanceNonZero(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deleteCardNode = mapper.createObjectNode();
        deleteCardNode.put("timestamp", command.getTimestamp());
        deleteCardNode.put("description",
                "Account couldn't be deleted - there are funds remaining");

        user.getTransactionHistory().add(deleteCardNode);
        return deleteCardNode;
    }

    /**
     * Logs a card deletion transaction into the user's transaction history.
     *
     * @param command the command containing the parameters.
     * @param sentUser the user owning the card.
     * @param account the acount associated with the card.
     * @param card the card that was destroyed.
     * @return an objectNode representing the transaction.
     */
    public ObjectNode addDeleteCardTransaction(final CommandInput command, final User sentUser,
                                               final Account account, final Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deleteCardNode = mapper.createObjectNode();
        deleteCardNode.put("timestamp", command.getTimestamp());
        deleteCardNode.put("description", "The card has been destroyed");
        deleteCardNode.put("card", card.getNumber());
        deleteCardNode.put("cardHolder", sentUser.getEmail());
        deleteCardNode.put("account", account.getAccountIBAN());

        sentUser.getTransactionHistory().add(deleteCardNode);
        return deleteCardNode;
    }
}
