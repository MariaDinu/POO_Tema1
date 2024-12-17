package org.poo.main.cards.cardTypes;

import org.poo.main.cards.Card;

public class OneTimePayCard extends Card {
    boolean hasPayed;

    public OneTimePayCard() {
        super();
        hasPayed = false;
    }

    @Override
    public void setHasPayed(boolean statement) {
        hasPayed = statement;
    }

    @Override
    public boolean getHasPayed() {
        return hasPayed;
    }
}
