package org.poo.main.coreBankingSystemComponents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.UserInput;
import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.transactions.UserHistoryTransactions;

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
     * Builds a JSON representation of the user's accounts.
     *
     * @return a JSON array node containing the account details.
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
     * Returns the first name of the user.
     *
     * @return the first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the new first name.
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the new last name.
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email of the user.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the new email address.
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Returns the list of accounts associated with the user.
     *
     * @return the list of accounts.
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Sets the list of accounts associated with the user.
     *
     * @param accounts the new list of accounts.
     */
    public void setAccounts(final List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Returns the transaction history of the user.
     *
     * @return the transaction history as a JSON array node.
     */
    public ArrayNode getTransactionHistory() {
        return transactionHistory;
    }
}
