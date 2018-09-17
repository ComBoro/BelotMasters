package net.comboro;

import net.comboro.belotbasics.Card;
import net.comboro.belotbasics.Type;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Declaration implements Comparable {

    public static final int TYPE_NONE = 0, TYPE_CONSEQUETIVE = 1, TYPE_SQUARE = 2;

    private int type = TYPE_NONE, points = 0;

    private Type highest;

    private List<Card> cards;

    public Declaration(List<Card> cards) {
        if (cards == null || cards.size() < 3) return;

        this.cards = cards;

        highest = cards.get(0).TYPE;
        boolean isSquare = true;
        for (Card card : cards) {
            if (!card.TYPE.equals(highest)) {
                isSquare = false;
                break;
            }
        }

        if (isSquare) {
            if (cards.size() != 4) return;

            type = TYPE_SQUARE;
            if (highest.equals(Type.Nine))
                points = 150;
            else if (highest.equals(Type.Jack))
                points = 200;
            else points = 100;
        } else {
            type = TYPE_CONSEQUETIVE;
            for (int i = 1; i < cards.size(); i++) {
                Type current = cards.get(i).TYPE;
                int prev_index = Type.DECLARATIONS.indexOf(highest);
                int this_index = Type.DECLARATIONS.indexOf(current);
                if (this_index != prev_index + 1) {
                    return;
                }
                highest = current;

                switch (cards.size()) {
                    case 5:
                        points = 100;
                        break;
                    case 4:
                        points = 50;
                        break;
                    case 3:
                        points = 20;
                        break;
                }
            }
        }

    }

    public static void filterStrongest(List<Declaration> dec1, List<Declaration> dec2) {
        // Consecutive
        List<Declaration>
                consDec1 = dec1.stream().filter(Declaration::isFromConsequetive).collect(Collectors.toList()),
                consDec2 = dec2.stream().filter(Declaration::isFromConsequetive).collect(Collectors.toList());

        Collections.sort(consDec1);
        Collections.sort(consDec2);

        Declaration
                dec1ConsStrongest = consDec1.get(consDec1.size() - 1),
                dec2ConsStrongest = consDec1.get(consDec1.size() - 1);

        int comparision = dec1ConsStrongest.compareTo(dec2ConsStrongest);

        if (comparision == 0) {
            //1 == 2
            consDec1.forEach(Declaration::nullify);
            consDec2.forEach(Declaration::nullify);
        } else if (comparision > 0) {
            //1 > 2
            consDec2.forEach(Declaration::nullify);
        } else {
            //2 > 1
            consDec1.forEach(Declaration::nullify);
        }

        // Square
        List<Declaration>
                squareDec1 = dec1.stream().filter(Declaration::isSquare).collect(Collectors.toList()),
                squareDec2 = dec2.stream().filter(Declaration::isSquare).collect(Collectors.toList());

        Collections.sort(squareDec1);
        Collections.sort(squareDec2);

        Declaration
                dec1SquareStrongest = consDec1.get(squareDec1.size() - 1),
                dec2SquareStrongest = consDec1.get(squareDec2.size() - 1);

        comparision = dec1SquareStrongest.compareTo(dec2SquareStrongest);

        if (comparision == 0) {
            squareDec1.forEach(Declaration::nullify);
            squareDec2.forEach(Declaration::nullify);
        } else if (comparision > 0) {
            squareDec2.forEach(Declaration::nullify);
        } else {
            squareDec1.forEach(Declaration::nullify);
        }

        // Combine
        dec1.clear();
        dec2.clear();

        dec1.addAll(consDec1);
        dec2.addAll(consDec2);

        dec1.addAll(squareDec1);
        dec2.addAll(squareDec2);

        // Cleanup
        consDec1.clear();
        consDec2.clear();
        squareDec1.clear();
        squareDec2.clear();

    }

    public Type getHighest() {
        return highest;
    }

    public int getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public void nullify() {
        this.type = TYPE_NONE;
        this.points = 0;
        this.highest = null;
    }

    public boolean isFromConsequetive() {
        return this.type == TYPE_CONSEQUETIVE;
    }

    public boolean isSquare() {
        return this.type == TYPE_SQUARE;
    }

    public boolean isNull() {
        return type == 0 || points == 0 || highest == null;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Declaration == false) {
            return 0;
        }
        Declaration other = (Declaration) o;
        if (isNull())
            return other.isNull() ? 0 : 1;

        return this.points == other.points ? Type.COMPARATOR_DECLARATIONS.compare(
                highest, other.highest) : this.points - other.points;

    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
