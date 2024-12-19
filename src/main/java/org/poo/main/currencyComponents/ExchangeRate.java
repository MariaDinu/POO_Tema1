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
     * Returns the source currency of the exchange rate.
     *
     * @return the source currency.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the source currency of the exchange rate.
     *
     * @param from the source currency.
     */
    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     * Returns the target currency of the exchange rate.
     *
     * @return the target currency.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the target currency of the exchange rate.
     *
     * @param to the target currency.
     */
    public void setTo(final String to) {
        this.to = to;
    }

    /**
     * Returns the conversion rate from the source currency to the target currency.
     *
     * @return the conversion rate.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Sets the conversion rate from the source currency to the target currency.
     *
     * @param rate the new conversion rate.
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }
}
