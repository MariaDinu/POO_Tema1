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

    public static class Builder {
        private String currency;
        private double interestRate = 0;
        private String accountIBAN;
        private double balance;
        private List<Card> cards;
        private double minBalance = 0;
        private String alias = "";
        private String type;

        public Builder(final String currency, final String type) {
            this.currency = currency;
            accountIBAN = Utils.generateIBAN();
            balance = 0;
            cards = new ArrayList<>();
            this.type = type;
        }

        /**
         *
         * @param interestRate
         * @return
         */
        public Builder setInterestRate(final double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        /**
         *
         * @param minBalance
         * @return
         */
        public Builder setMinBalance(final double minBalance) {
            this.minBalance = minBalance;
            return this;
        }

        /**
         *
         * @param alias
         * @return
         */
        public Builder setAlias(final String alias) {
            this.alias = alias;
            return this;
        }

        /**
         *
         * @return
         */
        public Account build() {
            return new Account(this);
        }
    }

    private Account(final Builder builder) {
        this.currency = builder.currency;
        this.interestRate = builder.interestRate;
        this.accountIBAN = builder.accountIBAN;
        this.balance = builder.balance;
        this.cards = builder.cards;
        this.minBalance = builder.minBalance;
        this.alias = builder.alias;
        this.type = builder.type;
    }

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
        return interestRate;
    }

    /**
     *
     * @param interestRate
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

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
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Account{"
                + "currency='"
                + currency
                + '\''
                + ", interestRate="
                + interestRate
                + ", accountIBAN='"
                + accountIBAN
                + '\''
                + ", balance="
                + balance
                + ", cards="
                + cards
                + ", minBalance="
                + minBalance
                + ", alias='"
                + alias
                + '\''
                + ", type='"
                + type
                + '\''
                + '}';
    }
}
