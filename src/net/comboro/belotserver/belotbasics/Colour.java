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
                    str.equalsIgnoreCase(colour.toShortString()) ||
                    str.equalsIgnoreCase(colour.toStringLetter()))
                return colour;
        }
        throw new IllegalArgumentException("No such colour '" + str + "'.");
    }

    @Override
    public String toString() {
        return string;
    }

    public String toShortString() {
        if (this.equals(Clubs)) {
            return String.valueOf('\u2667');
        } else if (this.equals(Diamonds)) {
            return String.valueOf('\u2662');
        } else if (this.equals(Hearts)) {
            return String.valueOf('\u2661');
        } else {
            return String.valueOf('\u2664');
        }
    }

    public String toStringLetter() {
        return toString().substring(0, 1);
    }

}
