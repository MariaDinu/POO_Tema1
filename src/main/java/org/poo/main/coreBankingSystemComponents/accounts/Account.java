package org.poo.main.coreBankingSystemComponents.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.transactions.BankingSystemTransactions;
import org.poo.main.coreBankingSystemComponents.cards.Card;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private String currency;
    private String accountIBAN;
    private double balance;
    private List<Card> cards;
    private double minBalance;
    private String alias;
    private ArrayNode transactionHistory;

    public Account(final String currency) {
        ObjectMapper objectMapper = new ObjectMapper();

        this.currency = currency;
        accountIBAN = Utils.generateIBAN();
        balance = 0;
        cards = new ArrayList<>();
        minBalance = 0;
        alias = "";
        this.transactionHistory = objectMapper.createArrayNode();
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param account
     * @param bankingSystem
     */
    public abstract void printSpendingReport(CommandInput command, ObjectNode objectNode,
                                             ArrayNode output, Account account,
                                             BankingSystem bankingSystem);

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param transactions
     */
    public abstract void addInterest(CommandInput command, ObjectNode objectNode,
                                     ArrayNode output, BankingSystemTransactions transactions);

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param transactions
     * @param account
     * @param bankingSystem
     */
    public abstract void changeInterestRate(CommandInput command, ObjectNode objectNode,
                                            ArrayNode output,
                                            BankingSystemTransactions transactions,
                                            Account account, BankingSystem bankingSystem);

    /**
     *
     * @return
     */
    public ArrayNode buildJsonCards() {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode cardsArray = mapper.createArrayNode();
        for (Card card : cards) {
            ObjectNode cardNode = mapper.createObjectNode();

            cardNode.put("cardNumber", card.getNumber());
            cardNode.put("status", card.getStatus());

            cardsArray.add(cardNode);
        }

        return cardsArray;
    }

    /**
     *
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     *
     * @param currency
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     *
     * @return
     */
    public double getInterestRate() {
        return 0.0;
    }

    /**
     *
     * @param interestRate
     */
    public void setInterestRate(final double interestRate) { }

    /**
     *
     * @return
     */
    public String getAccountIBAN() {
        return accountIBAN;
    }

    /**
     *
     * @param accountIBAN
     */
    public void setAccountIBAN(final String accountIBAN) {
        this.accountIBAN = accountIBAN;
    }

    /**
     *
     * @return
     */
    public double getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     */
    public void setBalance(final double balance) {
        this.balance = balance;
    }

    /**
     *
     * @return
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     *
     * @param cards
     */
    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

    /**
     *
     * @return
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     *
     * @param minBalance
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     *
     * @return
     */
    public String getAlias() {
        return alias;
    }

    /**
     *
     * @param alias
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     *
     * @return
     */
    public abstract String getType();

    /**
     *
     * @return
     */
    public ArrayNode getTransactionHistory() {
        return transactionHistory;
    }
}
