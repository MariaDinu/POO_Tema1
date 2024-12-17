package org.poo.main.cards.cardTypes;

import org.poo.main.cards.Card;

public class OneTimePayCard extends Card {
    private boolean hasPayed;

    public OneTimePayCard() {
        super();
        hasPayed = false;
    }

    /**
     *
     * @param statement
     */
    @Override
    public void setHasPayed(final boolean statement) {
        hasPayed = statement;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getHasPayed() {
        return hasPayed;
    }
}
