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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player {

    private final Object lock;
    protected Token token;
    private String username;
    protected BelotClient client;
    protected List<Card> cards = new ArrayList<>();
    private String input;
    private List<Declaration> declarations;
    private List<String> usedInDeclarations;
    private ScheduledExecutorService wait;
    private ScheduledFuture<?> future;

    public Player(Token token, BelotClient client) {
        this.token = token;
        this.username = token.getUsername();
        this.client = client;

        this.wait = Executors.newSingleThreadScheduledExecutor();
        this.future = null;
        this.lock = new Object();

        this.declarations = new ArrayList<>();
        this.usedInDeclarations = new ArrayList<>();

        if (client != null) {
            client.addListener(new ClientListener.ClientAdapter() {
                @Override
                public void onReceive(SerializableMessage<?> message) {
                    input = (String) message.getData();

                    System.out.println("RECEIVE " + input);

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
        } else System.err.println("NULL CLIENT");
    }

    public <M extends Serializable> void send(M message) {
        client.send(message);
    }

    public String waitForReply(String toSend, String defaultReply) {
        input = defaultReply;

        client.send(toSend);
        future = wait.schedule(lock::notify, Game.WAIT_TIME_PLAYER, TimeUnit.SECONDS);

        System.out.println("WAITING");

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
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

        Card card = Card.fromString(reply.substring(NetworkStringConstants.PREFIX_PLAY_CARD.length()));

        //cards.remove(card);

        return card;
    }

    public void addCard(Card card) {
        cards.add(card);
        //Notify client
        send("card:add:" + card);
    }

    public List<Declaration> getDeclarations() {
        List<Declaration> copy = new ArrayList<>(declarations);
        declarations.clear();
        usedInDeclarations.clear();
        return copy;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
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
