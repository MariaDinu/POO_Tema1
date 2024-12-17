package org.poo.main.cards.cardTypes;

import org.poo.main.cards.Card;

public class ClassicCard extends Card {

    public ClassicCard() {
        super();
    }

    @Override
    public void setHasPayed(boolean statement) {}

    @Override
    public boolean getHasPayed() {
        return false;
    }
}
