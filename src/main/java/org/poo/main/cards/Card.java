package org.poo.main.cards;

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
     *
     * @param statement
     */
    public abstract void setHasPayed(boolean statement);

    /**
     *
     * @return
     */
    public abstract boolean getHasPayed();

    /**
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     *
     * @return
     */
    public String getNumber() {
        return number;
    }

    /**
     *
     * @param number
     */
    public void setNumber(final String number) {
        this.number = number;
    }

    /**
     *
     * @return
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     *
     * @param frozen
     */
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    /**
     *
     * @return
     */
    public boolean isWarning() {
        return warning;
    }

    /**
     *
     * @param warning
     */
    public void setWarning(final boolean warning) {
        this.warning = warning;
    }
}
