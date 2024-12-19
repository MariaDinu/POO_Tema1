package org.poo.main.coreBankingSystemComponents.cards.cardFactory;

import org.poo.main.coreBankingSystemComponents.cards.Card;
import org.poo.main.coreBankingSystemComponents.cards.cardTypes.ClassicCard;
import org.poo.main.coreBankingSystemComponents.cards.cardTypes.OneTimePayCard;

public final class CardFactory {
    /**
     * Creates a card of the specified type.
     *
     * @param type the type of card to create.
     * @return an instance of the created card.
     * @throws IllegalArgumentException if the provided card type is not recognized.
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
