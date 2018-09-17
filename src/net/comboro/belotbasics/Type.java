package net.comboro.belotbasics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum Type {
    King(4, "K"), Queen(3, "Q"), Jack(20, 2, "J"), Ace(11, "A"), Ten(10, "10"), Nine(14, 0, "9"), Eight(0, "8"), Seven(0, "7");

    public static final List<Type>
            STRENGHT_NO_TRUMP = Arrays.asList(Seven, Eight, Nine, Jack, Queen, King, Ten, Ace),
            STRENGHT_TRUMP = Arrays.asList(Seven, Eight, Queen, King, Ten, Ace, Nine, Jack),
            DECLARATIONS = Arrays.asList(Seven, Eight, Nine, Ten, Jack, Queen, King, Ace);

    public static final Comparator<Type>
            COMPARATOR_NO_TRUMP = Comparator.comparingInt(STRENGHT_NO_TRUMP::indexOf),
            COMPARATOR_TRUMP = Comparator.comparingInt(STRENGHT_TRUMP::indexOf),
            COMPARATOR_DECLARATIONS = Comparator.comparingInt(DECLARATIONS::indexOf);


    private final int trump, notTrump;
    private final String string;

    Type(int trump, int notTrump, String string) {
        this.trump = trump;
        this.notTrump = notTrump;
        this.string = string;
    }

    Type(int all, String string) {
        this(all, all, string);
    }

    public static Type fromString(String str) {
        for (Type type : DECLARATIONS) {
            if (type.toString().equalsIgnoreCase(str))
                return type;
        }
        throw new IllegalArgumentException("No such type '" + str + "'.");
    }

    public int getTrump() {
        return trump;
    }

    public int getNotTrump() {
        return notTrump;
    }

    @Override
    public String toString() {
        return string;
    }
}
