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

    private UserHistoryTransactions transactions = new UserHistoryTransactions(this);

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
