package org.poo.main.coreBankingSystemComponents.cards.cardTypes;

import org.poo.main.coreBankingSystemComponents.cards.Card;

public class ClassicCard extends Card {

    public ClassicCard() {
        super();
    }

    /**
     * Sets whether the card has been used for payment.
     * This method is intentionally left empty as classic cards do not
     * track payment state.
     *
     * @param statement a boolean indicating the payment state.
     */
    @Override
    public void setHasPayed(final boolean statement) { }

    /**
     * Returns whether the card has been used for payment.
     * This method always returns false as classic cards do not
     * track payment state.
     *
     * @return false.
     */
    @Override
    public boolean getHasPayed() {
        return false;
    }
}
