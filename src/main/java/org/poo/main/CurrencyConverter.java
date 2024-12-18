package org.poo.main;
import java.util.*;

public class CurrencyConverter {

    private Map<String, List<ExchangeRate>> graph = new HashMap<>();

    // Constructs the graph
    public void constructGraph(List<ExchangeRate> exchangeRates) {
        for (ExchangeRate rate : exchangeRates) {
            graph.putIfAbsent(rate.getFrom(), new ArrayList<>());
            graph.putIfAbsent(rate.getTo(), new ArrayList<>());
            graph.get(rate.getFrom()).add(rate);
            graph.get(rate.getTo()).add(new ExchangeRate(rate.getTo(), rate.getFrom(), 1 / rate.getRate())); // Add reverse rate
        }
    }

    // Recursive method to find the conversion rate
    public double getRate(String from, String to, Set<String> visited) {
        if (!graph.containsKey(from)) return 0; // If the currency doesn't exist in the graph
        if (from.equals(to)) return 1; // Base case: converting to the same currency

        visited.add(from);

        for (ExchangeRate rate : graph.get(from)) {
            if (!visited.contains(rate.getTo())) {
                double conversionRate = getRate(rate.getTo(), to, visited);
                if (conversionRate != 0) {
                    return conversionRate * rate.getRate();
                }
            }
        }

        return 0; // No path found
    }

    // Public method to initiate the rate search
    public double getRate(String from, String to) {
        return getRate(from, to, new HashSet<>());
    }
}
