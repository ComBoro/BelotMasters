
package net.comboro.belotserver.belotbasics;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CardUtilsTest {

    private int gameMode = 3;
    private List<Card> playedCards = Arrays.asList("JD").stream().map(s -> Card.fromString(s)).collect(Collectors.toList());
    private List<Card> playerCards = Arrays.asList("10S", "10D", "KH", "QH", "AC", "8C", "10H").stream().map(s -> Card.fromString(s)).collect(Collectors.toList());

    @Test
    void hasTrump() {
    }

    @Test
    void hasFromAColour() {
    }

    @Test
    void canOvertrump() {
    }

    @Test
    void validMove() {
        assertFalse(CardUtils.validMove(playedCards, gameMode, playerCards, Card.fromString("10H")));
    }
}
