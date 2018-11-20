package net.comboro.belotserver.bots;

import net.comboro.belotserver.Game;
import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.belotbasics.CardUtils;
import net.comboro.belotserver.belotbasics.Colour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.comboro.belotserver.belotbasics.Colour.*;

public class BelotAI {

    public static int getAnnotation(List<Card> cards, int current_annot) {
        int annot = -1;
        for (Colour colour : new Colour[]{Clubs, Spades, Diamonds, Hearts}) {
            int count = (int) cards.stream().map(card -> card.COLOUR).filter(card_colour -> card_colour.equals(colour)).count();
            if (count > 2) {
                switch (colour) {
                    case Clubs:
                        annot = Game.GAME_MODE_CLUBS;
                        break;
                    case Hearts:
                        annot = Game.GAME_MODE_HEARTS;
                        break;
                    case Spades:
                        annot = Game.GAME_MODE_SPADES;
                        break;
                    case Diamonds:
                        annot = Game.GAME_MODE_DIAMONDS;
                        break;
                }
            }
        }
        return annot > current_annot ? annot : -1;
    }

    public static Card playCard(List<Card> laidCards, List<Card> playerCards, int gameMode) {
        System.out.println("pc " + playerCards);
        if (gameMode == -1) throw new IllegalArgumentException("Illegal game mode.");
        if (laidCards.size() > 3) throw new IllegalArgumentException("Way too many laid cards: " + laidCards);
        if (playerCards.isEmpty() || playerCards.size() > 8)
            throw new IllegalArgumentException("Illegal player cards: " + playerCards);
        List<Card> validMoves = new ArrayList<>();
        for (Card card : playerCards) {
            if (CardUtils.validMove(laidCards, gameMode, playerCards, card)) {
                validMoves.add(card);
            }
        }

        if (validMoves.size() == 0) {
            System.err.println(Thread.currentThread().getName() + " | No valid moves");
            System.err.println(Thread.currentThread().getName() + " | Player cards : " + playerCards.toString());
            System.err.println(Thread.currentThread().getName() + " | Laid cards : " + laidCards.toString());
            System.err.println(Thread.currentThread().getName() + " | Game Mode : " + gameMode);
            System.err.println(Thread.currentThread().getName() + " | ------------------------------------------");

            return playerCards.get(0);
        }

        int randCard = new Random().nextInt(validMoves.size());
        //playerCards.remove(randCard);
        return validMoves.get(randCard);
    }

}
