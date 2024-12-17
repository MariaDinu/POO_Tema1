package org.poo.main;

import org.poo.fileio.ExchangeInput;

public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(ExchangeInput exchangeRate) {
        from = exchangeRate.getFrom();
        to = exchangeRate.getTo();
        rate = exchangeRate.getRate();
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", rate=" + rate +
                '}';
    }
}
