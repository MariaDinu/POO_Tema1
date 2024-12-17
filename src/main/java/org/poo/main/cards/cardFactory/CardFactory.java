package org.poo.main.cards.cardFactory;

import org.poo.main.cards.Card;
import org.poo.main.cards.cardTypes.ClassicCard;
import org.poo.main.cards.cardTypes.OneTimePayCard;

public class CardFactory {
    public static Card createCard(String type) {
        switch (type) {
            case "Card": return new ClassicCard();
            case "OneTimeCard": return new OneTimePayCard();

            default:
                throw new IllegalArgumentException("The card type " + type
                        + " is not recognized.");
        }
    }

    private CardFactory() { }
}
