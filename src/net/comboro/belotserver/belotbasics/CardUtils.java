package net.comboro.belotserver.belotbasics;

import net.comboro.belotserver.Game;
import net.comboro.belotserver.Player;

import java.util.*;

public class CardUtils {

    public static final void sort(List<Card> cards) {
        if (cards.isEmpty()) return;

        Colour requestedColour = cards.get(0).COLOUR;

        Collections.sort(cards, (card1, card2) -> {
            if (card1.isTrump() && card2.isTrump()) return Type.COMPARATOR_TRUMP.compare(card1.TYPE, card2.TYPE);
            if (!card1.isTrump() && !card2.isTrump()) {
                if (card1.COLOUR.equals(requestedColour)) {
                    if (card2.COLOUR.equals(requestedColour))
                        return Type.COMPARATOR_NO_TRUMP.compare(card1.TYPE, card2.TYPE); //both are from requested colour
                    else return 1; // card 1 is from requested colour, card2 is not
                } else if (card2.COLOUR.equals(requestedColour))
                    return -1;// card2 is from requested colour, card1 is not
                else return 0;// Neither of the cards are from the requested colour
            }
            return card1.isTrump() ? 1 : -1; // One is trump the other is not
        });
    }

    public static Card getStronger(Card card1, Card card2) {
        if ((card1.isTrump() && card2.isTrump())
                || (!card1.isTrump() && !card2.isTrump()))
            return card1.getValue() > card2.getValue() ? card1 : card2;

        else return card1.isTrump() ? card1 : card2;
    }

    public static boolean hasTrump(List<Card> cards) {
        for (Card card : cards) if (card.isTrump()) return true;
        return false;
    }

    public static boolean hasFromAColour(List<Card> playerCards, Colour required) {
        for (Card card : playerCards)
            if (card.COLOUR.equals(required)) return true;
        return false;
    }

    public static boolean hasStrongerFromAColour(List<Card> playedCards, Colour requested, Type strongest) {
        for (Card card : playedCards)
            if (card.COLOUR.equals(requested)) {
                if (card.isTrump()) {
                    if (card.TYPE.getTrump() > strongest.getTrump()) return true;
                } else {
                    if (card.TYPE.getNotTrump() > card.TYPE.getNotTrump()) return true;
                }
            }
        return false;
    }

    public static boolean canOvertrump(List<Card> playerCards, Type strongestTrump) {
        for (Card card : playerCards) {
            if (card.isTrump() && card.getValue() > strongestTrump.getTrump())
                return true;
        }

        return false;
    }

    public static boolean validMove(List<Card> playedCards, int gameMode, List<Card> playerCards, Card toBePlayed) {
        if (playedCards == null || playedCards.isEmpty() || playerCards == null || playerCards.size() < 2) {
            return true;
        }

        int playedCardsSize = playedCards.size();

        if (!playerCards.contains(toBePlayed)) return false;

        Card firstCard = playedCards.get(0);

        Colour requestedColour = firstCard.COLOUR;
        boolean trumpRequired = firstCard.isTrump(),
                strongestPlayedByTeammate = false;

        Type strongestFromRequestedColour = firstCard.TYPE,
                strongestTrump = null;

        if (gameMode == Game.GAME_MODE_NO_TRUMP)
            // In No trump mode only the same colour is requested
            return !hasFromAColour(playerCards, requestedColour) || toBePlayed.COLOUR.equals(requestedColour);

        // Gather information from trick
        if (playedCardsSize > 1)
            for (int i = 1; i < playedCards.size(); i++) {
                Card card = playedCards.get(i);
                if (card.isTrump()) {
                    if (strongestTrump == null || card.TYPE.getTrump() > strongestTrump.getTrump()) {
                        strongestTrump = card.TYPE;
                        strongestPlayedByTeammate = (i + playedCardsSize % 2 == 0);
                    }
                } else {
                    if (card.TYPE.getNotTrump() > strongestFromRequestedColour.getNotTrump()) {
                        strongestFromRequestedColour = card.TYPE;
                        if (strongestTrump == null)
                            strongestPlayedByTeammate = (i + playedCardsSize % 2 == 0);
                    }
                }
            }


        if (gameMode == Game.GAME_MODE_ALL_TRUMP)
            if (hasFromAColour(playerCards, requestedColour)) { //Has from the requested colour
                if (toBePlayed.COLOUR.equals(requestedColour)) { // Plays from the requested colour
                    if (canOvertrump(playerCards, strongestTrump)) // If overtrumping is possible
                        return toBePlayed.TYPE.getTrump() > strongestTrump.getTrump(); // Has a stronger from the same colour, checking if it has been played
                    else return true; // Is from the same colour but can't overtrump - valid
                } else return false; // Has from the colour but plays another - invalid
            } else return true; // Doesn't have from the requested colour, so anything is valid

        // Game modes : 0-3
        if (hasFromAColour(playerCards, requestedColour)) {
            if (trumpRequired) { // Overtrumping is mandatory if possible
                if (canOvertrump(playerCards, strongestTrump))
                    return toBePlayed.isTrump() && toBePlayed.TYPE.getTrump() > strongestTrump.getTrump();
                return toBePlayed.isTrump();
            } else { // trump not required, anything is valid
                return toBePlayed.COLOUR.equals(requestedColour);
            }
        } else { // Doesn't have from the requested colour
            if (hasTrump(playerCards)) {
                if (!strongestPlayedByTeammate) {
                    if (canOvertrump(playerCards, strongestTrump))
                        return toBePlayed.isTrump() && toBePlayed.TYPE.getTrump() > strongestTrump.getTrump(); // If he can overtrump, he must
                }
                return true; // Teammate holds the trick, anything is valid
            } else return true; // Nor does he have a trump, anything is valid
        }
    }


    public static boolean validMove(List<Card> playedCards, int gameMode, Player player, Card toBePlayed) {
        List<Card> playerCards = player.getCards();

        return validMove(playedCards, gameMode, playerCards, toBePlayed);
    }

    public static int getPointsFromTricks(int trickPoints, int gameMode) {
        if (gameMode == Game.GAME_MODE_NO_TRUMP) trickPoints *= 2;
        int points = trickPoints / 10, remainder = trickPoints % 10;
        if (gameMode < 4) { // All suits and No trump
            if (remainder > 5) points++;
        } else if (gameMode == Game.GAME_MODE_ALL_TRUMP) {
            if (remainder > 3) {
                points++;
            }
        }

        return points;
    }

    public static ListIterator<Card> randomDeckListIterator() {
        return getRandomDeck().listIterator();
    }

    public static List<Card> getRandomDeck() {
        List<Card> deck = new ArrayList<>(Arrays.asList(
                new Card(Colour.Clubs, Type.Seven),
                new Card(Colour.Clubs, Type.Eight),
                new Card(Colour.Clubs, Type.Nine),
                new Card(Colour.Clubs, Type.Ten),
                new Card(Colour.Clubs, Type.Ace),
                new Card(Colour.Clubs, Type.Jack),
                new Card(Colour.Clubs, Type.Queen),
                new Card(Colour.Clubs, Type.King),

                new Card(Colour.Diamonds, Type.Seven),
                new Card(Colour.Diamonds, Type.Eight),
                new Card(Colour.Diamonds, Type.Nine),
                new Card(Colour.Diamonds, Type.Ten),
                new Card(Colour.Diamonds, Type.Ace),
                new Card(Colour.Diamonds, Type.Jack),
                new Card(Colour.Diamonds, Type.Queen),
                new Card(Colour.Diamonds, Type.King),

                new Card(Colour.Hearts, Type.Seven),
                new Card(Colour.Hearts, Type.Eight),
                new Card(Colour.Hearts, Type.Nine),
                new Card(Colour.Hearts, Type.Ten),
                new Card(Colour.Hearts, Type.Ace),
                new Card(Colour.Hearts, Type.Jack),
                new Card(Colour.Hearts, Type.Queen),
                new Card(Colour.Hearts, Type.King),

                new Card(Colour.Spades, Type.Seven),
                new Card(Colour.Spades, Type.Eight),
                new Card(Colour.Spades, Type.Nine),
                new Card(Colour.Spades, Type.Ten),
                new Card(Colour.Spades, Type.Ace),
                new Card(Colour.Spades, Type.Jack),
                new Card(Colour.Spades, Type.Queen),
                new Card(Colour.Spades, Type.King)
        ));
        Collections.shuffle(deck);
        return deck;
    }

}
