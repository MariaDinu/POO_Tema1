package org.poo.main;

import org.poo.fileio.ExchangeInput;

public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(final ExchangeInput exchangeRate) {
        from = exchangeRate.getFrom();
        to = exchangeRate.getTo();
        rate = exchangeRate.getRate();
    }

    public ExchangeRate(final String from, final String to, final double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ExchangeRate{"
                + "from='"
                + from
                + '\''
                + ", to='"
                + to
                + '\''
                + ", rate="
                + rate
                + '}';
    }
}
