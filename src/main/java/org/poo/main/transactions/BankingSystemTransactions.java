package org.poo.main.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.coreBankingSystemComponents.BankingSystem;
import org.poo.main.coreBankingSystemComponents.User;

public class BankingSystemTransactions {

    private BankingSystem bankingSystem;

    public BankingSystemTransactions(final BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    /**
     * Prints the transactions for a user up to the specified timestamp.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void printTransactions(final CommandInput command, final ObjectNode objectNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactions = objectMapper.createArrayNode();

        for (JsonNode jsonNode : bankingSystem.findUser(command.getEmail())
                .getTransactionHistory()) {
            if (jsonNode.get("timestamp").asInt() <= command.getTimestamp()) {
                transactions.add(jsonNode);
            }
        }

        objectNode.put("command", command.getCommand());
        objectNode.set("output", transactions);
        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response to print all users in the banking system.
     *
     * @param objectNode the objectNod} to store the JSON response.
     */
    public void buildJsonPrintUsers(final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "printUsers");

        ArrayNode usersArray = mapper.createArrayNode();
        for (User user : bankingSystem.getUsers()) {
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
     * Constructs a JSON response for a failed account deletion due to non-zero balance.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonDeleteAccountForNonZeroBalance(final CommandInput command,
                                                        final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "deleteAccount");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("error",
                "Account couldn't be deleted - see org.poo.transactions for details");
        outputArray.put("timestamp", command.getTimestamp());

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response for a successful account deletion.
     *
     * @param command the command containing the parameneters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonDeleteAccount(final CommandInput command, final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", "deleteAccount");

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("success", "Account deleted");
        outputArray.put("timestamp", command.getTimestamp());

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response for a failed online payment due to a missing card.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     * @param output the output to accumulate JSON responses.
     */
    public void buildJsonPayOnlineCardNotFound(final CommandInput command,
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
     * Constructs a JSON response for card status due to a missing card.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonCardStatusNotFound(final CommandInput command,
                                            final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Card not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response for an unsupported interest rate change operation, due to the
     * account not being a savings account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonChangeInterestRateNotSavings(final CommandInput command,
                                                      final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "This is not a savings account");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response for an unsupported add interest operation, due to the
     * account not being a savings account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonChangeAddInterestNotSavings(final CommandInput command,
                                                     final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "This is not a savings account");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Constructs a JSON response for a missing account.
     *
     * @param command the command containing the parameters.
     * @param objectNode the objectNode to store the JSON response.
     */
    public void buildJsonAccountNotFound(final CommandInput command,
                                         final ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();

        objectNode.put("command", command.getCommand());

        ObjectNode outputArray = mapper.createObjectNode();
        outputArray.put("timestamp", command.getTimestamp());
        outputArray.put("description", "Account not found");

        objectNode.set("output", outputArray);

        objectNode.put("timestamp", command.getTimestamp());
    }
}
