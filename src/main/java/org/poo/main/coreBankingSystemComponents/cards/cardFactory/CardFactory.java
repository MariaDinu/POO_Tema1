package org.poo.main.coreBankingSystemComponents.cards.cardFactory;

import org.poo.main.coreBankingSystemComponents.cards.Card;
import org.poo.main.coreBankingSystemComponents.cards.cardTypes.ClassicCard;
import org.poo.main.coreBankingSystemComponents.cards.cardTypes.OneTimePayCard;

public final class CardFactory {
    /**
     *
     * @param type
     * @return
     */
    public static Card createCard(final String type) {
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
