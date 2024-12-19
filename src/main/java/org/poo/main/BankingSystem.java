package org.poo.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.main.accounts.Account;
import org.poo.main.accounts.accountFactory.AccountFactory;
import org.poo.main.cards.Card;
import org.poo.main.cards.cardFactory.CardFactory;
import org.poo.main.paymentMethod.paymentTypes.OnlinePayment;
import org.poo.main.paymentMethod.PaymentSystem;
import org.poo.main.paymentMethod.paymentTypes.SplitPayment;
import org.poo.main.paymentMethod.paymentTypes.TransferPayment;

import java.util.*;

public class BankingSystem {
    private static BankingSystem instance = null;
    private List<User> users;
    private List<ExchangeRate> exchangeRates;

    private final List<ExchangeRate> currencyConverter = new ArrayList<>();

    public BankingSystem() { }

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

    /**
     *
     * @param commands
     * @param output
     */
    public void doCommands(final CommandInput[] commands, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();

        for (CommandInput command : commands) {
            String type;
            ObjectNode objectNode = mapper.createObjectNode();

            switch (command.getCommand()) {
                case "printUsers":
                    buildJsonPrintUsers(objectNode);
                    objectNode.put("timestamp", command.getTimestamp());
                    output.add(objectNode);
                    break;

                case "addAccount":
                    addAccount(command);
                    break;

                case "createCard":
                    type = "Card";
                    createCard(type, command);
                    break;

                case "addFunds":
                    addFunds(command.getAccount(), command.getAmount());
                    break;

                case "deleteAccount":
                    deleteAccount(command, objectNode);
                    output.add(objectNode);
                    break;

                case "createOneTimeCard":
                    type = "OneTimeCard";
                    createCard(type, command);
                    break;

                case "deleteCard":
                    deleteCard(command);
                    break;

                case "setMinimumBalance":
                    setMinimumBalance(command);
                    break;

                case "payOnline":
                    payOnline(command, objectNode, output);
                    break;

                case "sendMoney":
                    sendMoney(command);
                    break;

                case "setAlias":
                    setAlias(command);
                    break;

                case "printTransactions":
                    printTransactions(command, objectNode);
                    output.add(objectNode);
                    break;

                case "checkCardStatus":
                    checkCardStatus(command, objectNode, output);
                    break;

                case "changeInterestRate":
                    changeInterestRate(command, objectNode, output);
                    break;

                case "addInterest":
                    addInterest(command, objectNode, output);
                    break;

                case "splitPayment":
                    splitPayment(command);
                    break;

                case "report":
                    report(command, objectNode, output);
                    break;

                case "spendingsReport":
                    spendingReport(command, objectNode, output);
                    break;

                default:
            }
        }
    }


    /**
     *
     * @param command
     * @param objectNode
     * @param output
     */
    public void addInterest(final CommandInput command, final ObjectNode objectNode,
                             final ArrayNode output) {
        Account account = findAccount(command.getAccount());

        if (account == null) {
            System.out.println("NU S A GASIT ACCOUNT LA ADDINTERESTRATE");
            return;
        }

        if (!account.getType().equals("savings")) {
            buildJsonChangeAddInterestNotSavings(command, objectNode);
            output.add(objectNode);
        } else {
            account.addInterest();
        }
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     */
    public void changeInterestRate(final CommandInput command, final ObjectNode objectNode,
                                 final ArrayNode output) {
        Account account = findAccount(command.getAccount());

        if (account == null) {
            return;
        }

        if (!account.getType().equals("savings")) {
            buildJsonChangeInterestRateNotSavings(command, objectNode);
            output.add(objectNode);
        } else {
            account.setInterestRate(command.getInterestRate());
            User user = findUserOfAccount(account.getAccountIBAN());
            account.getTransactionHistory().add(user.addInterestRateChanged(command));
        }

    }

    /**
     *
     * @param command
     */
    public void checkCardStatus(final CommandInput command, final ObjectNode objectNode,
                                 final ArrayNode output) {
        Card card = findCard(command.getCardNumber());

        if (card == null) {
            buildJsonCardStatusNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        if (card.isFrozen()) {
            //DO NOTHING I GUESS
            return;
        }

        User user = findUserOfAccountOfCard(command.getCardNumber());
        Account account = findAccountOfCard(command.getCardNumber());

        if (account.getBalance() <= account.getMinBalance()) {
            card.setFrozen(true);
            card.setStatus("frozen");
            account.getTransactionHistory().add(user.addCardIsFrozenCheck(command));
        }

//        if (card.isWarning()) {
//            //POATE MAI TREBUIE CEVA AICI NU STIU INCA
//            card.setWarning(true);
//            card.setStatus("warning");
//        }


    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void spendingReport(final CommandInput command, final ObjectNode objectNode,
                                final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactions = objectMapper.createArrayNode();
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        ObjectNode outputArray = objectMapper.createObjectNode();

        // Check if account exists
        if (findAccount(command.getAccount()) == null) {
            buildJsonAccountNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        Account account = findAccount(command.getAccount());

        if (account.getType().equals("savings")) {
            outputArray.put("error", "This kind of report is not supported for a saving account");
            objectNode.put("command", command.getCommand());
            objectNode.set("output", outputArray);
            objectNode.put("timestamp", command.getTimestamp());
            output.add(objectNode);
            return;
        }

        outputArray.put("IBAN", account.getAccountIBAN());
        outputArray.put("balance", account.getBalance());
        outputArray.put("currency", account.getCurrency());

        Map<String, Double> commerciantTotals = new TreeMap<>();

        for (JsonNode jsonNode : findAccount(command.getAccount()).getTransactionHistory()) {
            int timestamp = jsonNode.get("timestamp").asInt();
            String description = jsonNode.get("description").asText();

            if (timestamp <= command.getEndTimestamp() && timestamp >= command.getStartTimestamp()
                    && "Card payment".equals(description)) {
                double amount = jsonNode.get("amount").asDouble();
                String commerciant = jsonNode.get("commerciant").asText();

                transactions.add(jsonNode);

                commerciantTotals.put(commerciant, commerciantTotals.getOrDefault(commerciant, 0.0) + amount);
            }
        }

        for (Map.Entry<String, Double> entry : commerciantTotals.entrySet()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", entry.getKey());
            commerciantNode.put("total", entry.getValue());
            commerciantsArray.add(commerciantNode);
        }

        outputArray.set("transactions", transactions);
        outputArray.set("commerciants", commerciantsArray);

        objectNode.put("command", command.getCommand());
        objectNode.set("output", outputArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void report(final CommandInput command, final ObjectNode objectNode,
                        final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactions = objectMapper.createArrayNode();
        ObjectNode outputArray = objectMapper.createObjectNode();

        if (findAccount(command.getAccount()) == null) {
            //System.out.println("NU S A GASIT ACCOUNT FOR " + command.getAccount());
            buildJsonAccountNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        Account account = findAccount(command.getAccount());
        outputArray.put("IBAN", account.getAccountIBAN());
        outputArray.put("balance", account.getBalance());
        outputArray.put("currency", account.getCurrency());

        for (JsonNode jsonNode : findAccount(command.getAccount()).getTransactionHistory()) {
            if (jsonNode.get("timestamp").asInt() <= command.getEndTimestamp() &&
                    jsonNode.get("timestamp").asInt() >= command.getStartTimestamp()) {
                transactions.add(jsonNode);
            }
        }

        outputArray.set("transactions", transactions);

        objectNode.put("command", command.getCommand());
        objectNode.set("output", outputArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void printTransactions(final CommandInput command, final ObjectNode objectNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactions = objectMapper.createArrayNode();

        for (JsonNode jsonNode : findUser(command.getEmail()).getTransactionHistory()) {
            if (jsonNode.get("timestamp").asInt() <= command.getTimestamp()) {
                transactions.add(jsonNode);
            }
        }

        objectNode.put("command", command.getCommand());
        objectNode.set("output", transactions);
        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     *
     * @param command
     */
    public void setAlias(final CommandInput command) {
        Account account = findAccount(command.getAccount());
        account.setAlias(command.getAlias());
    }

    /**
     *
     * @param command
     */
    public void splitPayment(final CommandInput command) {
        PaymentSystem paymentSystem = new PaymentSystem(this);
        paymentSystem.setPaymentStrategy(new SplitPayment(command));
        paymentSystem.makePayment();
    }

    /**
     *
     * @param command
     */
    public void sendMoney(final CommandInput command) {
        Account sender = findAccount(command.getAccount());
        Account receiver = findAccount(command.getReceiver());
        boolean isSenderAlias = sender.getAlias().equals(command.getAccount());

        if (sender ==  null || receiver == null || isSenderAlias) {
            return;
        }

        PaymentSystem paymentSystem = new PaymentSystem(this);
        paymentSystem.setPaymentStrategy(new TransferPayment(command));
        paymentSystem.makePayment();
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     */
    public void payOnline(final CommandInput command, final ObjectNode objectNode,
                           final ArrayNode output) {
        Card card = findCard(command.getCardNumber());

        if (card == null) {
            buildJsonPayOnlineCardNotFound(command, objectNode, output);
        } else if (card.isFrozen()) {
            User user = findUserOfAccountOfCard(card.getNumber());
            Account account = findAccountOfCard(card.getNumber());
            account.getTransactionHistory().add(user.addCardIsFrozen(command));
        } else {
            PaymentSystem paymentSystem = new PaymentSystem(this);
            paymentSystem.setPaymentStrategy(new OnlinePayment(command, objectNode, output));
            paymentSystem.makePayment();
        }
    }

    /**
     *
     * @param type
     * @param account
     */
    public void createCard(final String type, final int timestamp, final Account account) {
        Card card = CardFactory.createCard(type);
        account.getCards().add(card);

        User user = findUserOfAccount(account.getAccountIBAN());
        account.getTransactionHistory().add(user.addNewCardTransaction(timestamp, card, account, findUserOfAccount(account.getAccountIBAN())));
    }

    /**
     *
     * @param type
     * @param command
     */
    public void createCard(final String type, final CommandInput command) {
        if (findUserOfAccount(command.getAccount()) == null) {
            System.out.println("Unde e useru pt createCard");
            return;
        }

        if (!command.getEmail().equals(findUserOfAccount(command.getAccount()).getEmail())) {
            //TREBUIE SA SEMNALEZI IN TRANSACTIONHISTORY
        } else {
            Card card = CardFactory.createCard(type);
            Account account = findAccount(command.getAccount());
            account.getCards().add(card);

            User user = findUser(command.getEmail());
            account.getTransactionHistory().add(user.addNewCardTransaction(command, card));
        }
    }

    /**
     *
     * @param card
     */
    public void deleteOneTimeCard(final CommandInput command, final Card card) {
        if (findCard(card.getNumber()) == null) {
            //CARDUL A FOST STERS DEJA
            System.out.println("S a sters cardul oopsie la deleteCard");
        } else {
            Account account = findAccountOfCard(card.getNumber());
            User user = findUserOfAccount(account.getAccountIBAN());
            account.getCards().remove(card);
            account.getTransactionHistory().add(user.addDeleteCardTransaction(command, user, account, card));
        }
    }

    /**
     *
     * @param command
     */
    public void deleteCard(final CommandInput command) {
        if (findCard(command.getCardNumber()) == null) {
            //CARDUL A FOST STERS DEJA
            System.out.println("S a sters cardul oopsie");
        } else {
            User user = findUserOfAccountOfCard(command.getCardNumber());
            Account account = findAccountOfCard(command.getCardNumber());
            Card card = findCard(command.getCardNumber());

            account.getCards().remove(card);
            account.getTransactionHistory().add(user.addDeleteCardTransaction(command, user, account, card));
        }
    }
    public void deleteAccount(final CommandInput command, final ObjectNode objectNode) {
        if (findUserOfAccount(command.getAccount()) == null) {
            System.out.println("Unde e useru pt createCard");
            return;
        }

        if (!command.getEmail().equals(findUserOfAccount(command.getAccount()).getEmail())) {
            //TREBUIE SA SEMNALEZI IN TRANSACTIONHISTORY
        } else {
            User user = findUser(command.getEmail());
            Account account = findAccount(command.getAccount());

            if (account.getBalance() == 0.0) {
                user.getAccounts().remove(account);
                buildJsonDeleteAccount(command, objectNode);
            } else {
                buildJsonDeleteAccountForNonZeroBalance(command, objectNode);
                account.getTransactionHistory().add(user.addBalanceNonZero(command));
            }
        }
    }

    /**
     *
     * @param accountIBAN
     * @param amount
     */
    public void addFunds(final String accountIBAN, final double amount) {
        Account account = findAccount(accountIBAN);

        if (account == null) {
            System.out.println("Nu gasesc accountu frate");
        } else {
            account.setBalance(account.getBalance() + amount);
        }
    }

    /**
     *
     * @param command
     */
    public void setMinimumBalance(final CommandInput command) {
        Account account = findAccount(command.getAccount());
        account.setMinBalance(command.getAmount());
    }

    /**
     *
     * @param command
     */
    public void addAccount(final CommandInput command) {
        User user = findUser(command.getEmail());

        if (user == null) {
            System.out.println("Nu gasesc useru frate");
        } else {
            Account account = AccountFactory.createAccount(command.getAccountType(),
                    command.getCurrency(),
                    command.getInterestRate());

            user.getAccounts().add(account);
            account.getTransactionHistory().add(user.addNewAccountTransaction(command));
        }
    }

    /**
     *
     * @param accountIBAN
     * @return
     */
    public User findUserOfAccount(final String accountIBAN) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBAN)) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param cardNr
     * @return
     */
    public User findUserOfAccountOfCard(final String cardNr) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getNumber().equals(cardNr)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param cardNr
     * @return
     */
    public Account findAccountOfCard(final String cardNr) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getNumber().equals(cardNr)) {
                        return account;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param cardNr
     * @return
     */
    public Card findCard(final String cardNr) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getNumber().equals(cardNr)) {
                        return card;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param email
     * @return
     */
    public User findUser(final String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     *
     * @param accountIBAN
     * @return
     */
    public Account findAccount(final String accountIBAN) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBAN) || account.getAlias().equals(accountIBAN)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void buildJsonAccountNotFound(final CommandInput command,
                                          final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Account not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void buildJsonChangeAddInterestNotSavings(final CommandInput command,
                                                       final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "This is not a savings account");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void buildJsonChangeInterestRateNotSavings(final CommandInput command,
                                                       final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "This is not a savings account");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void buildJsonCardStatusNotFound(final CommandInput command,
                                             final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Card not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    public void buildJsonPayOnlineCardNotFound(final CommandInput command,
                                                final ObjectNode objectNode,
                                                final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "payOnline");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Card not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());

        output.add(objectNode);
    }

    /**
     *
     * @param command
     * @param objectNode
     */
    public void buildJsonDeleteAccountForNonZeroBalance(final CommandInput command,
                                                         final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "deleteAccount");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        outputArray.put("timestamp", command.getTimestamp());

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    public void buildJsonDeleteAccount(final CommandInput command, final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "deleteAccount");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("success", "Account deleted");
        outputArray.put("timestamp", command.getTimestamp());

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     *
     * @param objectNode
     */
    public void buildJsonPrintUsers(final ObjectNode objectNode) {
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

    /**
     *
     * @param users
     */
    public void setUsers(final UserInput[] users) {
        this.users = new ArrayList<>();

        for (UserInput user : users) {
            User customer = new User(user);
            this.users.add(customer);
        }
    }

    /**
     *
     * @param exchangeRates
     */
    public void setExchangeRates(final ExchangeInput[] exchangeRates) {
        this.exchangeRates = new ArrayList<>();

        for (ExchangeInput exchangeRate : exchangeRates) {
            ExchangeRate rate = new ExchangeRate(exchangeRate);
            this.exchangeRates.add(rate);
        }
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public double getExchangeRate(final String from, final String to) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.constructGraph(exchangeRates);

        return converter.getRate(from, to);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}


