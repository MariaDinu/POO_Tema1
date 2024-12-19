package org.poo.main.currencyComponents;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class CurrencyConverter {

    private Map<String, List<ExchangeRate>> graph = new HashMap<>();

    /**
     * Constructs a graph of exchange rates from the provided list.
     *
     * Each currency is represented as a node in the graph, and exchange rates
     * are represented as directed edges between the nodes. For each rate, a reverse
     * rate is also added to the graph.
     *
     * @param exchangeRates a list representing the exchange rates.
     */
    public void constructGraph(final List<ExchangeRate> exchangeRates) {
        for (ExchangeRate rate : exchangeRates) {
            graph.putIfAbsent(rate.getFrom(), new ArrayList<>());
            graph.putIfAbsent(rate.getTo(), new ArrayList<>());
            graph.get(rate.getFrom()).add(rate);
            graph.get(rate.getTo()).add(new ExchangeRate(rate.getTo(), rate.getFrom(),
                    1 / rate.getRate()));
        }
    }

    /**
     * Computes the conversion rate from one currency to another.
     *
     * @param from the source currency.
     * @param to the target currency.
     * @param visited a set of visited currencies to avoid cycles in the graph.
     * @return the conversion rate, or 0 if no path exists.
     */
    public double getRate(final String from, final String to, final Set<String> visited) {
        if (!graph.containsKey(from)) {
            return 0;
        }

        if (from.equals(to)) {
            return 1;
        }

        visited.add(from);

        for (ExchangeRate rate : graph.get(from)) {
            if (!visited.contains(rate.getTo())) {
                double conversionRate = getRate(rate.getTo(), to, visited);
                if (conversionRate != 0) {
                    return conversionRate * rate.getRate();
                }
            }
        }

        return 0;
    }

    /**
     * Computes the conversion rate from one currency to another.
     *
     * @param from the source currency.
     * @param to the target currency.
     * @return the conversion rate, or 0 if no path exists.
     */
    public double getRate(final String from, final String to) {
        return getRate(from, to, new HashSet<>());
    }
}
