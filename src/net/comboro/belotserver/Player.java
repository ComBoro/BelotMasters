package net.comboro.belotserver;

import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.networking.NetworkStringConstants;
import net.comboro.belotserver.networking.SerializableMessage;
import net.comboro.belotserver.networking.Token;
import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.networking.client.ClientListener;

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

    private Token token;
    private String username;
    private BelotClient client;
    private List<Card> cards = new LinkedList<>();
    private int team;

    private String input;
    private List<Declaration> declarations = new ArrayList<>();
    private List<String> usedInDeclarations = new ArrayList<>();

    public Player(Token token, BelotClient client, int team) {
        this.token = token;
        this.username = token.getUsername();
        this.client = client;
        this.team = team;

        if (client != null)
            client.addListener(new ClientListener.ClientAdapter() {
                @Override
                public void onReceive(SerializableMessage<?> message) {
                    input = (String) message.getData();

                    synchronized (lock) {
                        lock.notify();
                    }
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
        client.send(message);
    }

    public String waitForReply(String toSend, String defaultReply) {
        input = defaultReply;

        client.send(toSend);
        future = wait.schedule(() ->
                        lock.notify()
                , Game.WAIT_TIME_PLAYER, TimeUnit.SECONDS);

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {

            }
        }

        future = null;

        return input;
    }

    public Card waitForCard() {
        String reply = waitForReply(
                NetworkStringConstants.PREFIX_TIME_FOR_CARD + NetworkStringConstants.WAIT_TIME_PLAYER
                , "no-reply");


        if (reply.equals("no-reply")) {
            throw new RuntimeException('\"' + username + "\" didn't play a card.");
        }

        return Card.fromString(reply.substring(NetworkStringConstants.PREFIX_PLAY_CARD.length()));

//        if (reply.startsWith("card:")) {
//            reply = reply.substring(5);
//            try {
//                Card card = Card.fromString(reply);
//                // Check if card is valid
//                if (playedCards != null && !playedCards.isEmpty()) {
//                    if (!cards.contains(card))
//                        throw new RuntimeException('\"' + username + "\" tried to play a card not present in his hand.");
//                    boolean valid = CardUtils.validMove(playedCards, gameMode, this, card);
//                    if (!valid)
//                        throw new RuntimeException('\"' + username + "\" tried to play a card in a way that doesn't obey the rules.");
//                }
//                cards.remove(card);
//                return card;
//            } catch (IllegalArgumentException iae) {
//                throw new RuntimeException('\"' + username + "\" tried to play an invalid card: " + iae.getMessage());
//            }
//        } else throw new RuntimeException('\"' + username + "\" send an invalid command: " + reply);


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

    public Token getToken() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return this.client.equals(((Player) obj).client);
        } else return false;
    }
}
