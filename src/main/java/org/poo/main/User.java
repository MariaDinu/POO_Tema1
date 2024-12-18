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
