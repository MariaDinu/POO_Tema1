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
     * Generates a spending report for the account.
     *
     * @param command the input command containing parameters for the report.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param account the account for which the spending report is generated.
     * @param bankingSystem the banking system for verification and updates.
     */
    public abstract void printSpendingReport(CommandInput command, ObjectNode objectNode,
                                             ArrayNode output, Account account,
                                             BankingSystem bankingSystem);

    /**
     * Adds interest to the account.
     *
     * @param command the input command containing parameters.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param transactions the banking system transactions instance to build transactions.
     */
    public abstract void addInterest(CommandInput command, ObjectNode objectNode,
                                     ArrayNode output, BankingSystemTransactions transactions);

    /**
     * Changes the interest rate for the account.
     *
     * @param command the input command containing the parameters.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param transactions the banking system transactions instance to build transactions.
     * @param account the account for which the interest rate is changed.
     * @param bankingSystem the banking system instance for verification and updates.
     */
    public abstract void changeInterestRate(CommandInput command, ObjectNode objectNode,
                                            ArrayNode output,
                                            BankingSystemTransactions transactions,
                                            Account account, BankingSystem bankingSystem);

    /**
     * Builds a JSON representation of the cards associated with this account.
     *
     * @return a JSON array node containing the card details.
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
     * Returns the currency of the account.
     *
     * @return the currency.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of the account.
     *
     * @param currency the new currency.
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * Returns the interest rate for the account.
     *
     * @return the interest rate.
     */
    public double getInterestRate() {
        return 0.0;
    }

    /**
     * Sets the interest rate for the account.
     *
     * @param interestRate the new interest rate.
     */
    public void setInterestRate(final double interestRate) { }

    /**
     * Returns the IBAN of the account.
     *
     * @return the account IBAN.
     */
    public String getAccountIBAN() {
        return accountIBAN;
    }

    /**
     * Sets the IBAN of the account.
     *
     * @param accountIBAN the new account IBAN.
     */
    public void setAccountIBAN(final String accountIBAN) {
        this.accountIBAN = accountIBAN;
    }

    /**
     * Returns the balance of the account.
     *
     * @return the balance.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the balance of the account.
     *
     * @param balance the new balance.
     */
    public void setBalance(final double balance) {
        this.balance = balance;
    }

    /**
     * Returns the list of cards associated with the account.
     *
     * @return the list of cards.
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Sets the list of cards associated with the account.
     *
     * @param cards the new list of cards.
     */
    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Returns the minimum balance required for the account.
     *
     * @return the minimum balance.
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     * Sets the minimum balance required for the account.
     *
     * @param minBalance the new minimum balance.
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     * Returns the alias of the account.
     *
     * @return the alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of the account.
     *
     * @param alias the new alias.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Returns the type of the account.
     *
     * @return the account type as a string.
     */
    public abstract String getType();

    /**
     * Returns the transaction history of the account.
     *
     * @return the transaction history as a JSON array node.
     */
    public ArrayNode getTransactionHistory() {
        return transactionHistory;
    }
}
