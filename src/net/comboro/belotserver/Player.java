package net.comboro.belotserver;

import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.networking.NetworkStringConstants;
import net.comboro.belotserver.networking.SerializableMessage;
import net.comboro.belotserver.networking.Token;
import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.networking.client.ClientListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private final Object lock;
    protected Token token;
    private String username;
    protected BelotClient client;
    protected List<Card> cards;
    private String input;
    private List<Declaration> declarations;
    private List<String> usedInDeclarations;

    public Player(Token token, BelotClient client) {
        this.token = token;
        this.username = token.getUsername();
        this.client = client;

        this.cards = new ArrayList<>();

        this.lock = new Object();

        this.declarations = new ArrayList<>();
        this.usedInDeclarations = new ArrayList<>();

        if (client != null) {
            client.addListener(new ClientListener.ClientAdapter() {
                @Override
                public void onReceive(SerializableMessage<?> message) {
                    input = (String) message.getData();

//                    System.out.println("RECEIVE " + input);

                    synchronized (lock) {
                        lock.notify();
                    }

                    if (input.startsWith("declaration:")) {
                        String decl = input.substring(12);
                        String[] cardStrings = decl.split(",");
                        List<Card> cardsDec = new ArrayList<>(cardStrings.length);

                        boolean valid = true;

                        for (String string : cardStrings) {
                            if (usedInDeclarations.contains(string)) {
                                valid = false;
                                break;
                            }
                            usedInDeclarations.add(string);
                            cardsDec.add(Card.fromString(string));
                        }

                        //TODO if (valid && Game.roundID < 2) declarations.add(new Declaration(cards));
                    }
                }
            });
        } else System.err.println("NULL CLIENT");
    }

    public <M extends Serializable> void send(M message) {
        client.send(message);
    }

    public String waitForReply(String toSend, String defaultReply) {
        input = defaultReply;

        client.send(toSend);

//        System.out.println("WAITING");

        synchronized (lock) {
            try {
                lock.wait(Game.WAIT_TIME_PLAYER * 1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return input;
    }

    public Card waitForCard() {
        String reply = waitForReply(
                NetworkStringConstants.PREFIX_TIME_FOR_CARD + NetworkStringConstants.WAIT_TIME_PLAYER
                , "no-reply");


        if (reply.equals("no-reply")) {
            throw new RuntimeException('\"' + username + "\" didn't play a card.");
        }

        Card card = Card.fromString(reply.substring(NetworkStringConstants.PREFIX_PLAY_CARD.length()));

        cards.remove(card);

        return card;
    }

    public void addCard(Card card) {
        cards.add(card);
        //Notify client
        send("card:add:" + card);
    }

    protected boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public List<Declaration> getDeclarations() {
        List<Declaration> copy = new ArrayList<>(declarations);
        declarations.clear();
        usedInDeclarations.clear();
        return copy;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getUsername() {
        return username;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && this.client.equals(((Player) obj).client);
    }
}
