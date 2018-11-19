package net.comboro.belotserver.belotbasics;

import java.util.Arrays;

public enum Colour {

    Spades("Spades"), Diamonds("Diamonds"), Hearts("Hearts"), Clubs("Clubs");

    private final String string;

    Colour(String string) {
        this.string = string;
    }

    public static Colour fromString(String str) {
        for (Colour colour : Arrays.asList(
                Spades, Diamonds, Hearts, Clubs
        )) {
            if (str.equalsIgnoreCase(colour.string) ||
                    str.equalsIgnoreCase(colour.toShortString()))
                return colour;
        }
        throw new IllegalArgumentException("No such colour '" + str + "'.");
    }

    @Override
    public String toString() {
        return string;
    }

    public String toShortString() {
        return string.substring(0, 1);
    }

}
