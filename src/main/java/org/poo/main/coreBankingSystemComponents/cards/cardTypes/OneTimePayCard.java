package org.poo.main.coreBankingSystemComponents.cards.cardTypes;

import org.poo.main.coreBankingSystemComponents.cards.Card;

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
