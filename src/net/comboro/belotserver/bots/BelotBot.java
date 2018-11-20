package net.comboro.belotserver.bots;

import net.comboro.belotserver.Player;
import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.networking.SerializableMessage;
import net.comboro.belotserver.networking.Token;
import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.networking.client.ClientListener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static net.comboro.belotserver.networking.NetworkStringConstants.*;

public class BelotBot extends Player implements ClientListener {

    private String prefix;

    private List<Card> playedCards = new ArrayList<>();
    private int gameMode = -1;

    public BelotBot(Socket socket, int botId) throws IOException {
        super(
                new Token("usr" + botId, "pwd" + botId),
                new BelotClient(socket));

        String name = "Bot " + botId;
        this.client.setThreadName(name);
        this.prefix = name + "| ";

        this.client.addListener(this);

        send("token:" + token);
    }

    @Override
    public void onReceive(SerializableMessage<?> message) {
        String msg = (String) message.getData();
//        System.out.println(prefix + msg);
        //Adding cards
        if (msg.startsWith(PREFIX_ADD_CARD)) {
            cards.add(Card.fromString(msg.substring(PREFIX_ADD_CARD.length())));
        }

        // Round start
        if (msg.equals(ROUND_START)) {
            cards.clear();
            gameMode = -1;
        }

        if (msg.equals(TRICK_START))
            playedCards.clear();

        // Round gamemode
        if (msg.startsWith(ROUND_GAMEMODE)) {
            gameMode = Integer.parseInt(
                    msg.substring(ROUND_GAMEMODE.length())
            );
        }

        //Add played card
        if (msg.startsWith(PREFIX_PLAYED_CARD)) {
            String left = msg.substring(PREFIX_PLAYED_CARD.length());
            String split[] = left.split(SPLIT), player = split[0], card = split[1];
            playedCards.add(Card.fromString(card));
        }

        // Play card
        if (msg.startsWith(PREFIX_TIME_FOR_CARD)) {
            Card card = BelotAI.playCard(playedCards, cards, gameMode);
            System.out.println(cards);
            boolean removed = cards.remove(card);
            System.out.println("Removed " + card + " : " + removed);
            System.out.println(cards);

            // Send card
            send(PREFIX_PLAY_CARD + card.toString());
        }

        //Annotations
        if (msg.startsWith(PREFIX_ANNOTATION)) {
            int lastAnot = -1;
            if (msg.contains(",")) {
                String split[] = msg.split(",");
                lastAnot = Integer.parseInt(split[split.length - 1]);
            }
            int annot = BelotAI.getAnnotation(cards, lastAnot);
            System.out.println(prefix + "Annotation id: " + annot);
            send(PREFIX_ANNOTATION + annot);
        }

    }

    @Override
    public void onConnect() {
//        System.out.println(prefix + "onConnect");
    }

    @Override
    public void onDisconnect() {
//        System.out.println(prefix + "onDisconnect");
    }

    @Override
    public void onConnectionError(IOException io) {
        System.err.println(prefix + "onConnectionError | " + io.getMessage());
    }
}
