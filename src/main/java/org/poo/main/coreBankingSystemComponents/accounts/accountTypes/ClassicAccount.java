package org.poo.main.coreBankingSystemComponents.accounts.accountTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.transactions.BankingSystemTransactions;
import org.poo.main.coreBankingSystemComponents.accounts.Account;

import java.util.Map;
import java.util.TreeMap;

public class ClassicAccount extends Account {
    public ClassicAccount(final String currency) {
        super(currency);
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
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        ObjectNode outputArray = objectMapper.createObjectNode();

        outputArray.put("IBAN", account.getAccountIBAN());
        outputArray.put("balance", account.getBalance());
        outputArray.put("currency", account.getCurrency());

        Map<String, Double> commerciantTotals = new TreeMap<>();

        for (JsonNode jsonNode : bankingSystem.findAccount(command.getAccount())
                .getTransactionHistory()) {
            int timestamp = jsonNode.get("timestamp").asInt();
            String description = jsonNode.get("description").asText();

            if (timestamp <= command.getEndTimestamp() && timestamp >= command.getStartTimestamp()
                    && description.equals("Card payment")) {
                double amount = jsonNode.get("amount").asDouble();
                String commerciant = jsonNode.get("commerciant").asText();

                transactionsArray.add(jsonNode);

                commerciantTotals.put(commerciant, amount);
            }
        }

        for (Map.Entry<String, Double> entry : commerciantTotals.entrySet()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", entry.getKey());
            commerciantNode.put("total", entry.getValue());
            commerciantsArray.add(commerciantNode);
        }

        outputArray.set("transactions", transactionsArray);
        outputArray.set("commerciants", commerciantsArray);

        objectNode.put("command", command.getCommand());
        objectNode.set("output", outputArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    };

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
        transactions.buildJsonChangeAddInterestNotSavings(command, objectNode);
        output.add(objectNode);
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
        transactions.buildJsonChangeInterestRateNotSavings(command, objectNode);
        output.add(objectNode);
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "classic";
    }
}
