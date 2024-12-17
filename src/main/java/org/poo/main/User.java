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

    public User(UserInput user) {
        ObjectMapper objectMapper = new ObjectMapper();

        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        accounts = new ArrayList<>();
        transactionHistory = objectMapper.createArrayNode();
    }

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

    public void addNewAccountTransaction(CommandInput command) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newAccountNode = mapper.createObjectNode();
        newAccountNode.put("timestamp", command.getTimestamp());
        newAccountNode.put("description", "New account created");

        transactionHistory.add(newAccountNode);
    }

    public void addNewCardTransaction(CommandInput command, Card card) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode newCardNode = mapper.createObjectNode();
        newCardNode.put("timestamp", command.getTimestamp());
        newCardNode.put("description", "New card created");
        newCardNode.put("card", card.getNumber());
        newCardNode.put("cardHolder", command.getEmail());
        newCardNode.put("account", command.getAccount());

        transactionHistory.add(newCardNode);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
