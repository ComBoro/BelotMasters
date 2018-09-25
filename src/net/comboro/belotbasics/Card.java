package net.comboro.belotbasics;

import java.util.Collections;
import java.util.List;

public final class Card {

    public final Colour COLOUR;
    public final Type TYPE;
    private boolean trump;

    public Card(Colour colour, Type type) {
        this.COLOUR = colour;
        this.TYPE = type;
    }

    public static Card fromString(String string) {
        int length = string.length();
        return new Card(
                Colour.fromString(string.substring(length - 1)),
                Type.fromString(string.substring(0, length - 1))
        );
    }

    public static List<Card> fromString(String... cards) {
        List<Card> cardsList = Collections.EMPTY_LIST;
        for (String card : cards) cardsList.add(Card.fromString(card));
        return cardsList;
    }

    public Card setTrump() {
        this.trump = true;
        return this;
    }

    public boolean isTrump() {
        return Boolean.valueOf(trump);
    }

    public int getValue() {
        return trump ? TYPE.getTrump() : TYPE.getNotTrump();
    }

    @Override
    public String toString() {
        return TYPE + COLOUR.toShortString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            Card other = (Card) obj;
            return other.TYPE.equals(this.TYPE)
                    && other.COLOUR.equals(this.COLOUR);
        } else return false;
    }
}
