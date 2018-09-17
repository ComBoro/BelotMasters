package net.comboro.testing;

import net.comboro.Declaration;
import net.comboro.Player;
import net.comboro.belotbasics.Card;
import net.comboro.belotbasics.Colour;
import net.comboro.belotbasics.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<Card> cardList = Arrays.asList(
                new Card(Colour.Clubs, Type.Seven),
                new Card(Colour.Clubs, Type.Eight),
                new Card(Colour.Clubs, Type.Nine)
        );

        List<Card> cardList2 = Arrays.asList(
                new Card(Colour.Diamonds, Type.Eight),
                new Card(Colour.Diamonds, Type.Nine),
                new Card(Colour.Diamonds, Type.Ten)

        );

        Declaration dec1 = new Declaration(cardList), dec2 = new Declaration(cardList2);

        List<Declaration> dec = Arrays.asList(dec1, dec2);
        Collections.sort(dec);
        System.out.println(dec);

        System.out.println(dec1.compareTo(dec2));

        System.out.println(cardList);

        System.out.println();


        Player player1 = new Player("TEST_1", null, 1);
        for (Card card : Arrays.asList(
                new Card(Colour.Diamonds, Type.Eight),
                new Card(Colour.Diamonds, Type.Seven),
                new Card(Colour.Diamonds, Type.King),
                new Card(Colour.Diamonds, Type.Queen),

                new Card(Colour.Clubs, Type.Nine),
                new Card(Colour.Diamonds, Type.Nine),
                new Card(Colour.Hearts, Type.Nine),
                new Card(Colour.Spades, Type.Nine)
        )) {
            player1.addCard(card);
        }

        Player player2 = new Player("TEST_2", null, 2);
        for (Card card : Arrays.asList(
                new Card(Colour.Hearts, Type.King),
                new Card(Colour.Hearts, Type.Eight),
                new Card(Colour.Hearts, Type.Seven),
                new Card(Colour.Hearts, Type.Queen),

                new Card(Colour.Clubs, Type.Jack),
                new Card(Colour.Diamonds, Type.Jack),
                new Card(Colour.Hearts, Type.Jack),
                new Card(Colour.Spades, Type.Jack)
        )) {
            player2.addCard(card);
        }

    }

}
