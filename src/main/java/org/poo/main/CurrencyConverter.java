package org.poo.main;
import java.util.*;

public class CurrencyConverter {

    private Map<String, List<ExchangeRate>> graph = new HashMap<>();

    /**
     *
     * @param exchangeRates
     */
    public void constructGraph(final List<ExchangeRate> exchangeRates) {
        for (ExchangeRate rate : exchangeRates) {
            graph.putIfAbsent(rate.getFrom(), new ArrayList<>());
            graph.putIfAbsent(rate.getTo(), new ArrayList<>());
            graph.get(rate.getFrom()).add(rate);
            graph.get(rate.getTo()).add(new ExchangeRate(rate.getTo(), rate.getFrom(), 1 / rate.getRate())); // Add reverse rate
        }
    }

    /**
     *
     * @param from
     * @param to
     * @param visited
     * @return
     */
    public double getRate(final String from, final String to, final Set<String> visited) {
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

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public double getRate(final String from, final String to) {
        return getRate(from, to, new HashSet<>());
    }
}
