package net.comboro;

import net.comboro.belotbasics.Card;

import java.util.ArrayList;
import java.util.List;

import static net.comboro.NetworkStringConstants.*;

public class NetworkUtils {

    public static void sendTeam(Player player) {
        player.send(PREFIX_TEAM + player.getTeam());
    }

    public static void sendTeamOther(Player player, Player other) {
        player.send(PREFIX_PLAYER + other.getUsername() + SPLIT + other.getTeam());
    }

    public static String sendAnnotaionRequest(Player player) {
        return player.waitForReply(PREFIX_ANNOTATION + WAIT_TIME_PLAYER, PREFIX_ANNOTATION + "-1");
    }

    public static void sendLastPlayedCard(List<Player> allPlayers, Player cardOwner, Card card) {
        List<Player> copy = new ArrayList<>(allPlayers);
        copy.remove(cardOwner);
        copy.forEach(other -> other.send(
                PLAYED_CARD_PREFIX + cardOwner.getUsername() + SPLIT + card.toString()
        ));
    }

}
