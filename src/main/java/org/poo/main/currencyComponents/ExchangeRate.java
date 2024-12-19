package org.poo.main.currencyComponents;

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

    /**
     *
     * @return
     */
    public String getFrom() {
        return from;
    }

    /**
     *
     * @param from
     */
    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     *
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     *
     * @param to
     */
    public void setTo(final String to) {
        this.to = to;
    }

    /**
     *
     * @return
     */
    public double getRate() {
        return rate;
    }

    /**
     *
     * @param rate
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }
}
