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
     * Generates a spending report for the account based on the specified command and time range.
     * This kind of report is not supported for a savings account, so it generates an error.
     *
     * @param command the input command containing parameters for the report.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param account the account for which the spending report is generated.
     * @param bankingSystem the banking system for verification and updates..
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
     * Updates the interest rate for the account.
     *
     * @param interestRate the new interest rate to set for the account.
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Adds interest to the account. This method is not applicable to classic accounts.
     *
     * @param command the input command containing parameters.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param transactions the banking system transactions instance to build a transaction.
     */
    public void addInterest(final CommandInput command, final ObjectNode objectNode,
                            final ArrayNode output,
                            final BankingSystemTransactions transactions) {
        super.setBalance(super.getBalance() * interestRate);
    }

    /**
     * Changes the interest rate for the account. This method is not applicable to
     * classic accounts.
     *
     * @param command the input command containing the parameters.
     * @param objectNode the JSON object node to store the command output.
     * @param output the JSON array node to accumulate output.
     * @param transactions the banking system transactions instance to build a transaction.
     * @param account the account for which the interest rate is changed.
     * @param bankingSystem the banking system instance for verification and updates.
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
     * Returns the interest rate of the account.
     *
     * @return the interest rate of the account.
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Returns the type of the account as a string.
     *
     * @return the type of the account, which is "savings".
     */
    public String getType() {
        return "savings";
    }
}
