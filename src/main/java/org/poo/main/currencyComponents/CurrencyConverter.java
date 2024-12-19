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
     *
     * @param exchangeRates
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
     *
     * @param from
     * @param to
     * @param visited
     * @return
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
     *
     * @param from
     * @param to
     * @return
     */
    public double getRate(final String from, final String to) {
        return getRate(from, to, new HashSet<>());
    }
}
