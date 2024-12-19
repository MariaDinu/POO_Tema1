package org.poo.main.coreBankingSystemComponents.cards.cardTypes;

import org.poo.main.coreBankingSystemComponents.cards.Card;

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
