package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.main.accounts.Account;
import org.poo.main.cards.Card;
import org.poo.main.cards.cardFactory.CardFactory;

import java.util.ArrayList;
import java.util.List;

public class BankingSystem {
    private static BankingSystem instance = null;
    List<User> users;
    List<ExchangeRate> exchangeRates;
    //int timestamp;

    public BankingSystem() {
        users = new ArrayList<>();
        exchangeRates = new ArrayList<>();
    }

    /**
     * Singleton instance
     * @return the instance
     */
    public static BankingSystem getInstance() {
        if (instance == null) {
            instance = new BankingSystem();
        }
        return instance;
    }

    public void doCommands(CommandInput[] commands, ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();

        for (CommandInput command : commands) {
            ObjectNode objectNode = mapper.createObjectNode();

            switch (command.getCommand()) {
                case "printUsers":
                    buildJsonPrintUsers(objectNode);
                    objectNode.put("timestamp", command.getTimestamp());
                    output.add(objectNode);
                    break;

                case "addAccount":
                    if (command.getAccountType().equals("classic")) {
                        addClassicAccount(command);
                    } else {
                        addSavingsAccount(command);
                    }
                    break;

                case "createCard":
                    String type = "Card";
                    createCard(type, command);
                    break;

                case "addFunds":
                    addFunds(command.getAccount(), command.getAmount());
                    break;

                case "deleteAccount":
                    break;

                case "createOneTimeCard":
                    break;

                case "deleteCard":
                    break;

                case "setMinimumBalance":
                    break;

                case "payOnline":
                    break;

                case "sendMoney":
                    break;

                case "setAlias":
                    break;

                case "printTransactions":
                    break;

                case "checkCardStatus":
                    break;

                case "changeInterestRate":
                    break;

                case "splitPayment":
                    break;

                case "report":
                    break;

                case "spendingsReport":
                    break;

                default:
            }
        }
    }

    private void createCard(String type, CommandInput command) {
        if (!command.getEmail().equals(findUserOfAccount(command.getAccount()).getEmail())) {
            //TREBUIE SA SEMNALEZI IN TRANSACTIONHISTORY
        } else {
            Card card = CardFactory.createCard(type);
            Account account = findAccount(command.getAccount());
            account.getCards().add(card);

            User user = findUser(command.getEmail());
            user.addNewCardTransaction(command, card);
        }
    }
    private void addFunds(String accountIBAN, double amount) {
        Account account = findAccount(accountIBAN);

        //DELETE THIS AT THE END
        if (account == null)
            System.out.println("Nu gasesc accountu frate");
        //DELETE THIS AT THE END

        account.setBalance(account.getBalance() + amount);
    }

    private void addSavingsAccount(CommandInput command) {
        User user = findUser(command.getEmail());

        //DELETE THIS AT THE END
        if (user == null)
            System.out.println("Nu gasesc useru frate");
        //DELETE THIS AT THE END

        Account account = new Account.Builder(command.getCurrency(), command.getAccountType())
                .setInterestRate(command.getInterestRate())
                .build();

        user.getAccounts().add(account);
        user.addNewAccountTransaction(command);
    }

    private void addClassicAccount(CommandInput command) {
        User user = findUser(command.getEmail());

        //DELETE THIS AT THE END
        if (user == null)
            System.out.println("Nu gasesc useru frate");
        //DELETE THIS AT THE END

        Account account = new Account.Builder(command.getCurrency(), command.getAccountType())
                .build();

        user.getAccounts().add(account);
        user.addNewAccountTransaction(command);
    }

    private User findUserOfAccount(String accountIBAN) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBAN))
                    return user;
            }
        }
        return null;
    }

    private User findUser(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    private Account findAccount(String accountIBAN) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBAN))
                    return account;
            }
        }
        return null;
    }

    private void buildJsonPrintUsers(ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "printUsers");

        ArrayNode usersArray = mapper.createArrayNode();
        for (User user : users) {
            ObjectNode userNode = mapper.createObjectNode();

            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());
            userNode.set("accounts", user.buildJsonAccounts());

            usersArray.add(userNode);
        }

        objectNode.set("output", usersArray);
    }

    public void setUsers(final UserInput[] users) {
        for (UserInput user : users) {
            User customer = new User(user);
            this.users.add(customer);
        }
    }

    public void setExchangeRates(final ExchangeInput[] exchangeRates) {
        for (ExchangeInput exchangeRate : exchangeRates) {
            ExchangeRate rate = new ExchangeRate(exchangeRate);
            this.exchangeRates.add(rate);
        }
    }
}
