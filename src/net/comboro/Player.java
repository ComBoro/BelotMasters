package net.comboro;

import net.comboro.belotbasics.Card;
import net.comboro.belotbasics.CardUtils;
import net.comboro.networking.Client;
import net.comboro.networking.SerializableMessage;
import net.comboro.networking.internet.tcp.ClientTCP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player {

    private static ScheduledExecutorService wait = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> future = null;
    private static Object lock = new Object();

    private String username;
    private ClientTCP client;
    private List<Card> cards = new LinkedList<>();
    private int team;

    private String input;
    private List<Declaration> declarations = new ArrayList<>();
    private List<String> usedInDeclarations = new ArrayList<>();

    public Player(String username, ClientTCP client, int team) {
        this.username = username;
        this.client = client;
        this.team = team;
        //TODO Team.getById(team).addPlayer(this);

        if (client != null)
            client.addListener(new Client.ClientListener.ClientAdapter() {
                @Override
                public void onReceive(SerializableMessage<?> message) {
                    input = (String) message.getData();

                    lock.notify();
                    if (future != null) future.cancel(true);

                    if (input.startsWith("declaration:")) {
                        String decl = input.substring(12);
                        String[] cardStrings = decl.split(",");
                        List<Card> cards = new ArrayList<>(cardStrings.length);

                        boolean valid = true;

                        for (String string : cardStrings) {
                            if (usedInDeclarations.contains(string)) {
                                valid = false;
                                break;
                            }
                            usedInDeclarations.add(string);
                            cards.add(Card.fromString(string));
                        }

                        //TODO if (valid && Game.roundID < 2) declarations.add(new Declaration(cards));
                    }
                }
            });
    }

    public <M extends Serializable> void send(M message) {
        if (client != null)
            client.send(message);
    }

    public String waitForReply(String toSend, String defaultReply) {
        input = defaultReply;

        client.send(toSend);
        future = wait.schedule(() ->
                        lock.notify()
                , Game.WAIT_TIME_PLAYER, TimeUnit.SECONDS);

        try {
            lock.wait();
        } catch (InterruptedException e) {

        }

        future = null;

        return input;
    }

    public Card waitForCard() {
        return this.waitForCard(null, -1);
    }

    public Card waitForCard(List<Card> playedCards, int gameMode) {
        String reply = waitForReply(
                "card:" + (playedCards == null || playedCards.isEmpty() ? "-"
                        : playedCards.toString().substring(1, playedCards.size() - 1)
                        .replace(' ', '\u0000')
                ),
                "no-reply"
        );


        if (reply.equals("no-reply")) {
            throw new RuntimeException('\"' + username + "\" didn't play a card.");
        }

        try {
            Card card = Card.fromString(reply);
            // Check if card is valid
            if (playedCards != null && !playedCards.isEmpty()) {
                if (!cards.contains(card))
                    throw new RuntimeException('\"' + username + "\" tried to play a card not present in his hand.");
                boolean valid = CardUtils.validMove(playedCards, gameMode, this, card);
                if (!valid)
                    throw new RuntimeException('\"' + username + "\" tried to play a card in a way that doesn't obey the rules.");
            }
            cards.remove(card);
            return card;
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException('\"' + username + "\" tried to play an invalid card: " + iae.getMessage());
        }

    }

    public void addCard(Card card) {
        cards.add(card);
        //Notify client
        send("card:add:" + card);
    }

    public void removeCard(Card toRemove) {
        ListIterator<Card> cardListIterator = cards.listIterator();
        while (cardListIterator.hasNext()) {
            Card card = cardListIterator.next();
            if (card.TYPE.equals(toRemove.TYPE) && card.COLOUR.equals(toRemove.COLOUR)) {
                cardListIterator.remove();
                send("card:remove:" + card);
            }
        }
    }

    public List<Declaration> getDeclarations() {
        List<Declaration> copy = new ArrayList<>(declarations);
        declarations.clear();
        usedInDeclarations.clear();
        return copy;
    }

    public List<Card> getCards() {
        return new LinkedList<>(cards);
    }

    public int getTeamId() {
        return team;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return this.client.equals(((Player) obj).client);
        } else return false;
    }
}
