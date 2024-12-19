package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.main.accounts.Account;
import org.poo.main.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    private ArrayNode transactionHistory;

    public User(final UserInput user) {
        ObjectMapper objectMapper = new ObjectMapper();

        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        accounts = new ArrayList<>();
        transactionHistory = objectMapper.createArrayNode();
    }

    /**
     *
     * @return
     */
    public ArrayNode buildJsonAccounts() {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode accountsArray = mapper.createArrayNode();
        for (Account account : accounts) {
            ObjectNode accountNode = mapper.createObjectNode();

            accountNode.put("IBAN", account.getAccountIBAN());
            accountNode.put("balance", account.getBalance());
            accountNode.put("currency", account.getCurrency());
            accountNode.put("type", account.getType());
            accountNode.set("cards", account.buildJsonCards());

            accountsArray.add(accountNode);
        }

        return accountsArray;
    }

    /**
     *
     * @param command
     */
    public void addNewAccountTransaction(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newAccountNode = mapper.createObjectNode();
        newAccountNode.put("timestamp", command.getTimestamp());
        newAccountNode.put("description", "New account created");

        transactionHistory.add(newAccountNode);
    }

    /**
     *
     * @param command
     * @param card
     */
    public void addNewCardTransaction(final CommandInput command, final Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", command.getTimestamp());
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", command.getEmail());
        newCardNode.put("account", command.getAccount());

        transactionHistory.add(newCardNode);
    }

    /**
     *
     * @param command
     */
    public void addPayOnlineInsufficientFunds(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Insufficient funds");

        transactionHistory.add(newPayOnlineNode);
    }

    /**
     *
     * @param command
     */
    public void addSplitPaymentTransaction(final CommandInput command) {
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

        transactionHistory.add(newSplitPaymentNode);
    }

    /**
     * @param command
     * @param accounts
     * @param accountIBAN
     */
    public void addSplitPaymentError(final CommandInput command, final List<String> accounts,
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

        transactionHistory.add(newSplitPaymentNode);
    }

    /**
     *
     * @param command
     */
    public void addInterestRateChanged(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newInterestRateNode = mapper.createObjectNode();
        newInterestRateNode.put("timestamp", command.getTimestamp());
        newInterestRateNode.put("description", "Interest rate of the account changed to "
                        + command.getInterestRate());

        transactionHistory.add(newInterestRateNode);
    }

    /**
     *
     * @param command
     */
    public void addSendMoneyInsufficientFunds(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Insufficient funds");

        transactionHistory.add(newPayOnlineNode);
    }

    /**
     *
     * @param command
     */
    public void addCardIsFrozenCheck(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newFrozenCardNode = mapper.createObjectNode();
        newFrozenCardNode.put("timestamp", command.getTimestamp());
        newFrozenCardNode.put("description", "You have reached the " +
                "minimum amount of funds, the card will be frozen");

        transactionHistory.add(newFrozenCardNode);
    }

    /**
     *
     * @param command
     */
    public void addCardIsFrozen(final CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newFrozenCardNode = mapper.createObjectNode();
        newFrozenCardNode.put("timestamp", command.getTimestamp());
        newFrozenCardNode.put("description", "The card is frozen");

        transactionHistory.add(newFrozenCardNode);
    }

    /**
     *
     * @param command
     * @param pay
     */
    public void addPayOnlinePayment(final CommandInput command, final double pay) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newPayOnlineNode = mapper.createObjectNode();
        newPayOnlineNode.put("timestamp", command.getTimestamp());
        newPayOnlineNode.put("description", "Card payment");
        newPayOnlineNode.put("amount", pay);
        newPayOnlineNode.put("commerciant", command.getCommerciant());

        transactionHistory.add(newPayOnlineNode);
    }

    /**
     *
     * @param command
     * @param sender
     * @param receiver
     */
    public void addSendMoneyTransaction(final CommandInput command, final Account sender,
                                        final Account receiver) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newSendMoneyNode = mapper.createObjectNode();
        newSendMoneyNode.put("timestamp", command.getTimestamp());
        newSendMoneyNode.put("description", command.getDescription());
        newSendMoneyNode.put("senderIBAN", sender.getAccountIBAN());
        newSendMoneyNode.put("receiverIBAN", receiver.getAccountIBAN());
        newSendMoneyNode.put("amount", command.getAmount() + " " + sender.getCurrency());
        newSendMoneyNode.put("transferType", "sent");

        transactionHistory.add(newSendMoneyNode);
    }

    /**
     *
     * @param timestamp
     * @param card
     * @param account
     * @param user
     */
    public void addNewCardTransaction(final int timestamp, final Card card,
                                      final Account account, final User user) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", timestamp);
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", user.getEmail());
        newCardNode.put("account", account.getAccountIBAN());

        transactionHistory.add(newCardNode);
    }

    public void addDeleteCardTransaction(final CommandInput command, final User user,
                                         final Account account, final Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode deleteCardNode = mapper.createObjectNode();
        deleteCardNode.put("timestamp", command.getTimestamp());
        deleteCardNode.put("description", "The card has been destroyed");
        deleteCardNode.put("card", card.getNumber());
        deleteCardNode.put("cardHolder", user.getEmail());
        deleteCardNode.put("account", account.getAccountIBAN());

        transactionHistory.add(deleteCardNode);
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     *
     * @param accounts
     */
    public void setAccounts(final List<Account> accounts) {
        this.accounts = accounts;
    }

    public ArrayNode getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(ArrayNode transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "User{"
                + "firstName='"
                + firstName
                + '\''
                + ", lastName='"
                + lastName
                + '\''
                + ", email='"
                + email
                + '\''
                + ", accounts="
                + accounts
                + '}';
    }
}
