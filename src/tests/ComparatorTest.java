package tests;

import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.belotbasics.CardUtils;
import net.comboro.belotserver.belotbasics.Colour;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ComparatorTest {

    @Test
    public void printTrumps() {
        List<Card> cards = Card.fromString("10S", "KS", "AS", "8S");
        for (Card card : cards)
            if (card.COLOUR.equals(Colour.Spades))
                card.setTrump();
        CardUtils.sortAscending(cards);
        System.out.println(cards);
        CardUtils.sortDescending(cards);
        System.out.println(cards);
    }

}
