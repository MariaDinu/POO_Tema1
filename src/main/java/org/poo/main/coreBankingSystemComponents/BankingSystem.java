package org.poo.main.coreBankingSystemComponents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.main.coreBankingSystemComponents.accounts.Account;
import org.poo.main.coreBankingSystemComponents.accounts.accountFactory.AccountFactory;
import org.poo.main.coreBankingSystemComponents.cards.Card;
import org.poo.main.coreBankingSystemComponents.cards.cardFactory.CardFactory;
import org.poo.main.currencyComponents.CurrencyConverter;
import org.poo.main.currencyComponents.ExchangeRate;
import org.poo.main.paymentMethod.paymentTypes.OnlinePayment;
import org.poo.main.paymentMethod.PaymentSystem;
import org.poo.main.paymentMethod.paymentTypes.SplitPayment;
import org.poo.main.paymentMethod.paymentTypes.TransferPayment;
import org.poo.main.transactions.BankingSystemTransactions;
import org.poo.main.transactions.UserHistoryTransactions;

import java.util.List;
import java.util.ArrayList;


public class BankingSystem {
    private static BankingSystem instance = null;
    private List<User> users;
    private List<ExchangeRate> exchangeRates;
    private BankingSystemTransactions transactions = new BankingSystemTransactions(this);

    public BankingSystem() { }

    /**
     * Retrieves the singleton instance of the banking system.
     *
     * @return the singleton instance of the banking system.
     */
    public static BankingSystem getInstance() {
        if (instance == null) {
            instance = new BankingSystem();
        }
        return instance;
    }

    /**
     * Processes an array of commands and generates the corresponding output.
     *
     * @param commands the array of command to be processed.
     * @param output the arrayNode to store the output of the commands.
     */
    public void doCommands(final CommandInput[] commands, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();

        for (CommandInput command : commands) {
            String type;
            ObjectNode objectNode = mapper.createObjectNode();

            switch (command.getCommand()) {
                case "printUsers":
                    transactions.buildJsonPrintUsers(objectNode);
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
                    transactions.printTransactions(command, objectNode);
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
     * Adds interest to an account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void addInterest(final CommandInput command, final ObjectNode objectNode,
                             final ArrayNode output) {
        Account account = findAccount(command.getAccount());

        if (account == null) {
            return;
        }

        account.addInterest(command, objectNode, output, transactions);
    }

    /**
     * Changes the interest rate of an account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void changeInterestRate(final CommandInput command, final ObjectNode objectNode,
                                 final ArrayNode output) {
        Account account = findAccount(command.getAccount());

        if (account == null) {
            return;
        }

        account.changeInterestRate(command, objectNode, output, transactions, account, this);
    }

    /**
     * Checks the status of a card and updates its state if necessary.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void checkCardStatus(final CommandInput command, final ObjectNode objectNode,
                                 final ArrayNode output) {
        Card card = findCard(command.getCardNumber());

        if (card == null) {
            transactions.buildJsonCardStatusNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        if (card.isFrozen()) {
            return;
        }

        User user = findUserOfAccountOfCard(command.getCardNumber());
        Account account = findAccountOfCard(command.getCardNumber());
        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);

        if (account.getBalance() <= account.getMinBalance()) {
            card.setFrozen(true);
            card.setStatus("frozen");
            account.getTransactionHistory().add(userHistory.addCardIsFrozenCheck(command));
        }
    }

    /**
     * Generates a spending report for a specified account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void spendingReport(final CommandInput command, final ObjectNode objectNode,
                                final ArrayNode output) {
        if (findAccount(command.getAccount()) == null) {
            transactions.buildJsonAccountNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        Account account = findAccount(command.getAccount());
        account.printSpendingReport(command, objectNode, output, account, this);
    }

    /**
     * Generates a detailed report for a specified account over a time range.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void report(final CommandInput command, final ObjectNode objectNode,
                        final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ObjectNode outputArray = objectMapper.createObjectNode();

        if (findAccount(command.getAccount()) == null) {
            transactions.buildJsonAccountNotFound(command, objectNode);
            output.add(objectNode);
            return;
        }

        Account account = findAccount(command.getAccount());
        outputArray.put("IBAN", account.getAccountIBAN());
        outputArray.put("balance", account.getBalance());
        outputArray.put("currency", account.getCurrency());

        for (JsonNode jsonNode : findAccount(command.getAccount()).getTransactionHistory()) {
            int timestamp = jsonNode.get("timestamp").asInt();

            if (timestamp <= command.getEndTimestamp()
                    && timestamp >= command.getStartTimestamp()) {
                transactionsArray.add(jsonNode);
            }
        }

        outputArray.set("transactions", transactionsArray);

        objectNode.put("command", command.getCommand());
        objectNode.set("output", outputArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    }

    /**
     * Sets an alias for a specified account.
     *
     * @param command the command containing the parameters.
     */
    public void setAlias(final CommandInput command) {
        Account account = findAccount(command.getAccount());
        account.setAlias(command.getAlias());
    }

    /**
     * Processes a split payment transaction.
     *
     * @param command the command containing the parameters.
     */
    public void splitPayment(final CommandInput command) {
        PaymentSystem paymentSystem = new PaymentSystem(this);
        paymentSystem.setPaymentStrategy(new SplitPayment(command));
        paymentSystem.makePayment();
    }

    /**
     * Processes a money transfer transaction.
     *
     * @param command the command containing the parameters.
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
     * Processes a card payment transaction.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     * @param output the arrayNode to store the output.
     */
    public void payOnline(final CommandInput command, final ObjectNode objectNode,
                           final ArrayNode output) {
        Card card = findCard(command.getCardNumber());

        if (card == null) {
            transactions.buildJsonPayOnlineCardNotFound(command, objectNode, output);
            return;
        }

        if (card.isFrozen()) {
            User user = findUserOfAccountOfCard(card.getNumber());
            Account account = findAccountOfCard(card.getNumber());

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            account.getTransactionHistory().add(userHistory.addCardIsFrozen(command));
            return;
        }

        PaymentSystem paymentSystem = new PaymentSystem(this);
        paymentSystem.setPaymentStrategy(new OnlinePayment(command, objectNode, output));
        paymentSystem.makePayment();
    }

    /**
     * Creates a card of the specified type and associates it with an account.
     *
     * @param type  the type of card to create.
     * @param timestamp the timestamp of the card creation.
     * @param account the account to associate the card with.
     */
    public void createCard(final String type, final int timestamp, final Account account) {
        Card card = CardFactory.createCard(type);
        account.getCards().add(card);

        User user = findUserOfAccount(account.getAccountIBAN());

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory()
                .add(userHistory.addNewCardTransaction(timestamp,
                        card, account, findUserOfAccount(account.getAccountIBAN())));
    }

    /**
     * Creates a card of the specified type using command input and associates it with an account.
     *
     * @param type the type of card to create.
     * @param command the command containing the parameters.
     */
    public void createCard(final String type, final CommandInput command) {
        if (!command.getEmail().equals(findUserOfAccount(command.getAccount()).getEmail())) {
            return;
        }

        Card card = CardFactory.createCard(type);
        Account account = findAccount(command.getAccount());
        account.getCards().add(card);

        User user = findUser(command.getEmail());

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory().add(userHistory.addNewCardTransaction(command, card));
    }

    /**
     * Deletes a one-time card from the account it is associated with.
     *
     * @param command the command containing the parameters.
     * @param card the card to be deleted.
     */
    public void deleteOneTimeCard(final CommandInput command, final Card card) {
        Account account = findAccountOfCard(card.getNumber());
        User user = findUserOfAccount(account.getAccountIBAN());
        account.getCards().remove(card);

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory()
                .add(userHistory.addDeleteCardTransaction(command, user, account, card));
    }

    /**
     * Deletes a card specified by the command input.
     *
     * @param command the command containing the parameters.
     */
    public void deleteCard(final CommandInput command) {
        if (findCard(command.getCardNumber()) == null) {
            return;
        }

        User user = findUserOfAccountOfCard(command.getCardNumber());
        Account account = findAccountOfCard(command.getCardNumber());
        Card card = findCard(command.getCardNumber());

        account.getCards().remove(card);

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory()
                .add(userHistory.addDeleteCardTransaction(command, user, account, card));

    }

    /**
     * Deletes an account specified by the command input.
     * If the account balance is non-zero, it logs an error and does not delete the account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the response.
     */
    public void deleteAccount(final CommandInput command, final ObjectNode objectNode) {
        User user = findUser(command.getEmail());
        Account account = findAccount(command.getAccount());

        if (account.getBalance() == 0.0) {
            user.getAccounts().remove(account);
            transactions.buildJsonDeleteAccount(command, objectNode);
        } else {
            transactions.buildJsonDeleteAccountForNonZeroBalance(command, objectNode);

            UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
            account.getTransactionHistory().add(userHistory.addBalanceNonZero(command));
        }
    }

    /**
     * Adds funds to a specified account.
     *
     * @param accountIBAN the IBAN of the account to which funds will be added.
     * @param amount the amount to be added to the account.
     */
    public void addFunds(final String accountIBAN, final double amount) {
        Account account = findAccount(accountIBAN);
        account.setBalance(account.getBalance() + amount);
    }

    /**
     * Sets the minimum balance for a specified account.
     *
     * @param command the command containing the parameters.
     */
    public void setMinimumBalance(final CommandInput command) {
        Account account = findAccount(command.getAccount());
        account.setMinBalance(command.getAmount());
    }

    /**
     * Adds a new account for a user based on the command details.
     *
     * @param command the command containing the parameters.
     */
    public void addAccount(final CommandInput command) {
        User user = findUser(command.getEmail());

        Account account = AccountFactory.createAccount(command.getAccountType(),
                command.getCurrency(),
                command.getInterestRate());

        user.getAccounts().add(account);

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory().add(userHistory.addNewAccountTransaction(command));
    }

    /**
     * Finds the user associated with a specific account IBAN.
     *
     * @param accountIBAN the IBAN of the account.
     * @return the user associated with the account, or null if not found.
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
     * Finds the user associated with a specific card number.
     *
     * @param cardNr the card number.
     * @return the user associated with the card, or null if not found.
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
     * Finds the account associated with a specific card number.
     *
     * @param cardNr the card number.
     * @return the account associated with the card, or null if not found.
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
     * Finds the card based on its number.
     *
     * @param cardNr the card number.
     * @return the card with the specified number, or null if not found.
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
     * Finds the user based on their email address.
     *
     * @param email the email address of the user.
     * @return the user with the specified email, or null if not found.
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
     * Finds the account based on its IBAN or alias.
     *
     * @param accountIBANOrAlias the IBAN or alias of the account.
     * @return the account with the specified IBAN or alias, or null if not found.
     */
    public Account findAccount(final String accountIBANOrAlias) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBANOrAlias)
                        || account.getAlias().equals(accountIBANOrAlias)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Sets the list of users in the banking system from an array of user inputs.
     *
     * @param users the array of user objects to initialize users.
     */
    public void setUsers(final UserInput[] users) {
        this.users = new ArrayList<>();

        for (UserInput user : users) {
            User customer = new User(user);
            this.users.add(customer);
        }
    }

    /**
     * Sets the list of exchange rates in the banking system from an array of exchange inputs.
     *
     * @param exchangeRates the array of exchange input objects to initialize exchange rates.
     */
    public void setExchangeRates(final ExchangeInput[] exchangeRates) {
        this.exchangeRates = new ArrayList<>();

        for (ExchangeInput exchangeRate : exchangeRates) {
            ExchangeRate rate = new ExchangeRate(exchangeRate);
            this.exchangeRates.add(rate);
        }
    }

    /**
     * Retrieves the exchange rate between two currencies.
     *
     * @param from the source currency code.
     * @param to the target currency code.
     * @return the exchange rate from the source to the target currency.
     */
    public double getExchangeRate(final String from, final String to) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.constructGraph(exchangeRates);

        return converter.getRate(from, to);
    }

    /**
     * Retrieves the list of users in the banking system.
     *
     * @return a list of user objects.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users in the banking system.
     *
     * @param users a list of user objects to be set.
     */
    public void setUsers(final List<User> users) {
        this.users = users;
    }

    /**
     * Retrieves the list of exchange rates in the banking system.
     *
     * @return a list of exchange rate objects.
     */
    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    /**
     * Sets the list of exchange rates in the banking system.
     *
     * @param exchangeRates a list of exchange rate objects to be set.
     */
    public void setExchangeRates(final List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}


