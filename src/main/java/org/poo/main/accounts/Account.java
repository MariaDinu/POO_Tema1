package org.poo.main.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.cards.Card;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String currency;
    private double interestRate;
    private String accountIBAN;
    private double balance;
    private List<Card> cards;
    private double minBalance;
    private String alias;
    private String type;
    private ArrayNode transactionHistory;

    public static class Builder {
        private String currency;
        private double interestRate = 0;
        private String accountIBAN;
        private double balance;
        private List<Card> cards;
        private double minBalance = 0;
        private String alias = "";
        private String type;

        public Builder(String currency, String type) {
            this.currency = currency;
            accountIBAN = Utils.generateIBAN();
            balance = 0;
            cards = new ArrayList<>();
            this.type = type;
        }

        public Builder setInterestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public Builder setMinBalance(double minBalance) {
            this.minBalance = minBalance;
            return this;
        }

        public Builder setAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }

    private Account(Builder builder) {
        this.currency = builder.currency;
        this.interestRate = builder.interestRate;
        this.accountIBAN = builder.accountIBAN;
        this.balance = builder.balance;
        this.cards = builder.cards;
        this.minBalance = builder.minBalance;
        this.alias = builder.alias;
        this.type = builder.type;
    }

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String getAccountIBAN() {
        return accountIBAN;
    }

    public void setAccountIBAN(String accountIBAN) {
        this.accountIBAN = accountIBAN;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(double minBalance) {
        this.minBalance = minBalance;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayNode getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(ArrayNode transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    @Override
    public String toString() {
        return "Account{" +
                "currency='" + currency + '\'' +
                ", interestRate=" + interestRate +
                ", accountIBAN='" + accountIBAN + '\'' +
                ", balance=" + balance +
                ", cards=" + cards +
                ", minBalance=" + minBalance +
                ", alias='" + alias + '\'' +
                ", type='" + type + '\'' +
                ", transactionHistory=" + transactionHistory +
                '}';
    }
}
