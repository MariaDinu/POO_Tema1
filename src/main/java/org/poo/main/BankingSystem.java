package org.poo.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.main.accounts.Account;
import org.poo.main.cards.Card;
import org.poo.main.cards.cardFactory.CardFactory;

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
                    if (command.getAccountType().equals("classic")) {
                        addClassicAccount(command);
                    } else {
                        addSavingsAccount(command);
                    }
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

    /**
     *
     * @param command
     */
    private void checkCardStatus(final CommandInput command, final ObjectNode objectNode,
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
            user.addCardIsFrozenCheck(command);
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
    private void printTransactions(final CommandInput command, final ObjectNode objectNode) {
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
    private void setAlias(final CommandInput command) {
        if (!findUser(command.getEmail()).equals(findUserOfAccount(command.getAccount()))) {
            //PROPRIETARUL CONTULUI SI UTILIZATORUL NU SUNT ACEEASI
            System.out.println("PROPRIETARUL CONTULUI SI UTILIZATORUL NU SUNT ACEEASI");
        } else {
            Account account = findAccount(command.getAccount());
            account.setAlias(command.getAlias());
        }
    }

    /**
     *
     * @param command
     */
    private void sendMoney(final CommandInput command) {
        //System.out.println(command.getAccount());
        //System.out.println(command.getReceiver());
        //System.out.println();

        Account sender = findAccount(command.getAccount());
        Account receiver = findAccount(command.getReceiver());
        boolean isSenderAlias = sender.getAlias().equals(command.getAccount());

        if (sender ==  null || receiver == null || isSenderAlias) {
            //UN ACCOUNT NU EXISTA
            System.out.println("DONDE EXISTAS ACCOUNT");
            return;
        }

        double rate = getExchangeRate(sender.getCurrency(), receiver.getCurrency());
        double pay = command.getAmount() * rate;

        //System.out.println(rate);
        //System.out.println(pay);
        //System.out.println();


        if (sender.getBalance() < command.getAmount()) {
            //NU ARE DESTUI BANI
            //System.out.println("NOT ENOUGHT MONEY SENDMONEY!");

            //TREBUIE SA ADAUGI INSUFFICIENT FUNDS IN HISTORY
            User user = findUserOfAccount(sender.getAccountIBAN());
            user.addSendMoneyInsufficientFunds(command);
        } else {
            //System.out.println(sender);
            //System.out.println(receiver);
            //System.out.println(sender.getCurrency() + " " + receiver.getCurrency() + " " + rate
            //+ " " + command.getAmount());
            //System.out.println();

            sender.setBalance(sender.getBalance() - command.getAmount());
            receiver.setBalance(receiver.getBalance() + pay);

            if (receiver.getBalance() > receiver.getMinBalance() + 30) {
                for (Card card : receiver.getCards()) {
                    card.setWarning(false);
                }
            }

            //System.out.println(sender);
            //System.out.println(receiver);
            //System.out.println();

            User user = findUserOfAccount(sender.getAccountIBAN());
            user.addSendMoneyTransaction(command, sender, receiver);
        }
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     */
    private void payOnline(final CommandInput command, final ObjectNode objectNode,
                           final ArrayNode output) {
        Card card = findCard(command.getCardNumber());

        if (card == null) {
            //CARDUL A FOST STERS
            //System.out.println("Cardul e sters");
            //CRED CA NU MAI TREBUIE SA FAC NIMIC AICI
            buildJsonPayOnlineCardNotFound(command, objectNode, output);
            //System.out.println("CARD NOT FOUND BITCH");
        } else if (card.isFrozen()) {
            //NU SE POATE PLATI PT E BLOCAT CARDUL
            User user = findUserOfAccountOfCard(card.getNumber());
            user.addCardIsFrozen(command);
            //System.out.println("CARD IS BLOCKED BITCH");
        } else {
            if(!command.getEmail().equals(findUserOfAccountOfCard(command.getCardNumber()).getEmail())) {
                //NU E PROPRIETARUL CARDULUI
                System.out.println("Nu e prop cardului");
            } else {
                double rate = getExchangeRate(command.getCurrency(),
                        findAccountOfCard(command.getCardNumber()).getCurrency());

                double pay = command.getAmount() * rate;

                if (findAccountOfCard(command.getCardNumber()).getBalance() < pay) {
                    //NU ARE DESTUI BANI
                    User user = findUserOfAccountOfCard(card.getNumber());
                    user.addPayOnlineInsufficientFunds(command);
                } else if (findAccountOfCard(command.getCardNumber()).getBalance() <
                        findAccountOfCard(command.getCardNumber()).getMinBalance()) {
                    //NU MAI AI VOIE LA BANI
                    System.out.println("NU MAI AI VOIE LA BANI! REFUZ PLATA");
                } else {
                    Account account = findAccountOfCard(command.getCardNumber());

                    //System.out.println(account);
                    //System.out.println(account.getCurrency() + " " + command.getCurrency() + " " + rate
                    //        + " " + command.getAmount());
                    //System.out.println();

                    account.setBalance(account.getBalance() - pay);

                    //System.out.println(account);
                    //System.out.println();

                    //ADAUGARE IN HISTORYT OF THIS
                    User user = findUserOfAccountOfCard(card.getNumber());
                    user.addPayOnlinePayment(command, pay);



                    //ONETIMEPAY CARD
                    card.setHasPayed(true);
                    if (card.getHasPayed()) {
                        //deleteCard(command);
                        createCard("OneTimeCard", command.getTimestamp(), account);
                        card.setFrozen(true);
                    }

                    /*if (account.getBalance() <= (account.getMinBalance() + 30)) {
                        card.setWarning(true);
                        //POATE CEVA IN HISTORYT????
                    }

                    if (account.getBalance() <= account.getMinBalance()) {
                        card.setFrozen(true);
                        //POATE CEVA IN HISTORYT????
                    }*/
                }
            }
        }
    }

    /**
     *
     * @param type
     * @param account
     */
    private void createCard(final String type, final int timestamp, final Account account) {
        Card card = CardFactory.createCard(type);
        account.getCards().add(card);

        User user = findUserOfAccount(account.getAccountIBAN());
        user.addNewCardTransaction(timestamp, card, account, findUserOfAccount(account.getAccountIBAN()));
    }

    /**
     *
     * @param type
     * @param command
     */
    private void createCard(final String type, final CommandInput command) {
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
            user.addNewCardTransaction(command, card);
        }
    }

    private void deleteCard(final CommandInput command) {
        if (findCard(command.getCardNumber()) == null) {
            //CARDUL A FOST STERS DEJA
            System.out.println("S a sters cardul oopsie");
        } else {
            User user = findUserOfAccountOfCard(command.getCardNumber());
            Account account = findAccountOfCard(command.getCardNumber());
            Card card = findCard(command.getCardNumber());

            account.getCards().remove(card);
            user.addDeleteCardTransaction(command, user, account, card);
        }
    }
    private void deleteAccount(final CommandInput command, final ObjectNode objectNode) {
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
            }
        }
    }

    /**
     *
     * @param accountIBAN
     * @param amount
     */
    private void addFunds(final String accountIBAN, final double amount) {
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
    private void setMinimumBalance(final CommandInput command) {
        Account account = findAccount(command.getAccount());
        account.setMinBalance(command.getAmount());
    }

    /**
     *
     * @param command
     */
    private void addSavingsAccount(final CommandInput command) {
        User user = findUser(command.getEmail());

        if (user == null) {
            System.out.println("Nu gasesc useru frate");
        } else {

            Account account = new Account.Builder(command.getCurrency(), command.getAccountType())
                    .setInterestRate(command.getInterestRate())
                    .build();

            user.getAccounts().add(account);
            user.addNewAccountTransaction(command);
        }
    }

    /**
     *
     * @param command
     */
    private void addClassicAccount(final CommandInput command) {
        User user = findUser(command.getEmail());

        if (user == null) {
            System.out.println("Nu gasesc useru frate");
        } else {
            Account account = new Account.Builder(command.getCurrency(), command.getAccountType())
                    .build();

            user.getAccounts().add(account);
            user.addNewAccountTransaction(command);
        }
    }

    /**
     *
     * @param accountIBAN
     * @return
     */
    private User findUserOfAccount(final String accountIBAN) {
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
    private User findUserOfAccountOfCard(final String cardNr) {
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
    private Account findAccountOfCard(final String cardNr) {
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
    private Card findCard(final String cardNr) {
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
    private User findUser(final String email) {
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
    private Account findAccount(final String accountIBAN) {
        for (User user : users) {
            for (Account account: user.getAccounts()) {
                if (account.getAccountIBAN().equals(accountIBAN) || account.getAlias().equals(accountIBAN)) {
                    return account;
                }
            }
        }
        return null;
    }

    private void buildJsonCardStatusNotFound(final CommandInput command,
                                             final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Card not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    private void buildJsonPayOnlineCardNotFound(final CommandInput command,
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
    private void buildJsonDeleteAccountForNonZeroBalance(final CommandInput command,
                                                         final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "deleteAccount");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        outputArray.put("timestamp", command.getTimestamp());

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    private void buildJsonDeleteAccount(final CommandInput command, final ObjectNode objectNode) {
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
    private void buildJsonPrintUsers(final ObjectNode objectNode) {
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


