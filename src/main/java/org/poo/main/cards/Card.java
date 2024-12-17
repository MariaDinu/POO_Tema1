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

    public abstract void setHasPayed(boolean statement);

    public abstract boolean getHasPayed();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }
}
