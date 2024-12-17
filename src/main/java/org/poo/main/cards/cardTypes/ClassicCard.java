package org.poo.main.cards.cardTypes;

import org.poo.main.cards.Card;

public class ClassicCard extends Card {

    public ClassicCard() {
        super();
    }

    /**
     *
     * @param statement
     */
    @Override
    public void setHasPayed(final boolean statement) { }

    /**
     *
     * @return
     */
    @Override
    public boolean getHasPayed() {
        return false;
    }
}
