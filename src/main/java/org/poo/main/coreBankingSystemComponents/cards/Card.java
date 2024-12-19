package org.poo.main.coreBankingSystemComponents.cards;

import org.poo.utils.Utils;

public abstract class Card {
    private String number;
    private String status;
    private boolean frozen;
    private boolean warning;

    public Card() {
        number = Utils.generateCardNumber();
        status = "active";
        frozen = false;
        warning = false;
    }

    /**
     * Sets whether the card has been used for payment.
     *
     * @param statement a boolean indicating the payment state.
     */
    public abstract void setHasPayed(boolean statement);

    /**
     * Returns whether the card has been used for payment.
     *
     * @return true if the card has been used for payment, otherwise false.
     */
    public abstract boolean getHasPayed();

    /**
     * Returns the current status of the card.
     *
     * @return the card status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the card.
     *
     * @param status the new status of the card.
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Returns the card number.
     *
     * @return the card number.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns whether the card is frozen.
     *
     * @return true if the card is frozen, otherwise false.
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Sets whether the card is frozen.
     *
     * @param frozen a boolean indicating the frozen state of the card.
     */
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }
}
