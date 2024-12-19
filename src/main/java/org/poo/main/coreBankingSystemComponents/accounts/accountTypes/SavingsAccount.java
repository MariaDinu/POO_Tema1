package org.poo.main.coreBankingSystemComponents.accounts.accountTypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.transactions.BankingSystemTransactions;
import org.poo.main.coreBankingSystemComponents.User;
import org.poo.main.transactions.UserHistoryTransactions;
import org.poo.main.coreBankingSystemComponents.accounts.Account;

public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final String currency, final double interestRate) {
        super(currency);
        this.interestRate = interestRate;
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param account
     * @param bankingSystem
     */
    public void printSpendingReport(final CommandInput command,
                                    final ObjectNode objectNode,
                                    final ArrayNode output,
                                    final Account account,
                                    final BankingSystem bankingSystem) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode outputArray = objectMapper.createObjectNode();

        outputArray.put("error", "This kind of report is not supported for a saving account");
        objectNode.put("command", command.getCommand());
        objectNode.set("output", outputArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    };

    /**
     *
     * @param interestRate
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param transactions
     */
    public void addInterest(final CommandInput command, final ObjectNode objectNode,
                            final ArrayNode output,
                            final BankingSystemTransactions transactions) {
        super.setBalance(super.getBalance() * interestRate);
    }

    /**
     *
     * @param command
     * @param objectNode
     * @param output
     * @param transactions
     * @param account
     * @param bankingSystem
     */
    public void changeInterestRate(final CommandInput command, final ObjectNode objectNode,
                                   final ArrayNode output,
                                   final BankingSystemTransactions transactions,
                                   final Account account,
                                   final BankingSystem bankingSystem) {
        account.setInterestRate(command.getInterestRate());
        User user = bankingSystem.findUserOfAccount(account.getAccountIBAN());

        UserHistoryTransactions userHistory = new UserHistoryTransactions(user);
        account.getTransactionHistory().add(userHistory.addInterestRateChanged(command));
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
     * @return
     */
    public String getType() {
        return "savings";
    }
}
