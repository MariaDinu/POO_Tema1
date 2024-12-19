package org.poo.main.coreBankingSystemComponents.cards.cardTypes;

import org.poo.main.coreBankingSystemComponents.cards.Card;

public class OneTimePayCard extends Card {
    private boolean hasPayed;

    public OneTimePayCard() {
        super();
        hasPayed = false;
    }

    /**
     * Sets whether the card has been used for payment.
     *
     * @param statement a boolean indicating the payment state.
     */
    @Override
    public void setHasPayed(final boolean statement) {
        hasPayed = statement;
    }

    /**
     * Returns whether the card has been used for payment.
     *
     * @return true if the card has been used for payment, otherwise false.
     */
    @Override
    public boolean getHasPayed() {
        return hasPayed;
    }
}
