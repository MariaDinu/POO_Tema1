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

        account.addInterest(command, objectNode, output, transactions);
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

        account.changeInterestRate(command, objectNode, output, transactions, account, this);
    }

    /**
     *
     * @param command
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
     *
     * @param command
     * @param objectNode
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
     *
     * @param command
     * @param objectNode
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
     *
     * @param type
     * @param account
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
     *
     * @param type
     * @param command
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
     *
     * @param card
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
     *
     * @param command
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
     *
     * @param command
     * @param objectNode
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
     *
     * @param accountIBAN
     * @param amount
     */
    public void addFunds(final String accountIBAN, final double amount) {
        Account account = findAccount(accountIBAN);
        account.setBalance(account.getBalance() + amount);
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

        Account account = AccountFactory.createAccount(command.getAccountType(),
                command.getCurrency(),
                command.getInterestRate());

        user.getAccounts().add(account);

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory().add(userHistory.addNewAccountTransaction(command));
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
                if (account.getAccountIBAN().equals(accountIBAN)
                        || account.getAlias().equals(accountIBAN)) {
                    return account;
                }
            }
        }
        return null;
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

    /**
     *
     * @return
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     */
    public void setUsers(final List<User> users) {
        this.users = users;
    }

    /**
     *
     * @return
     */
    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    /**
     *
     * @param exchangeRates
     */
    public void setExchangeRates(final List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}

