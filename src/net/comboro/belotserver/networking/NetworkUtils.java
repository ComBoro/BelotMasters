package net.comboro.belotserver.networking;

import net.comboro.belotserver.Player;
import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.team.Teams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.comboro.belotserver.networking.NetworkStringConstants.*;

public class NetworkUtils {

    public static void sendTeam(Player player, Teams teams) {
        player.send(PREFIX_TEAM + teams.getTeam(player).getTeamId());
    }

    public static void sendTeamOther(Player player, Player other, Teams teams) {
        player.send(PREFIX_PLAYER + other.getUsername() + SPLIT + teams.getTeam(other).getTeamId());
    }

    public static void sendTeammates(Teams teams) {
        List<Player> playerList = teams.getPlayerList();
        for (Player player : playerList) {
            List<Player> copy = new ArrayList<>(teams.getPlayerList());
            copy.remove(player);
            NetworkUtils.sendTeam(player, teams);
            for (Player other : copy) {
                NetworkUtils.sendTeamOther(player, other, teams);
            }
        }
    }

    public static String sendAnnotaionRequest(Player player, List<Integer> previousAnnots) {
        String annot = PREFIX_ANNOTATION + WAIT_TIME_PLAYER;

        if (previousAnnots == null || previousAnnots.isEmpty())
            return player.waitForReply(annot, PREFIX_ANNOTATION + "-1");

        for (int prevAnot : previousAnnots) {
            annot += "," + prevAnot;
        }

        return player.waitForReply(annot, PREFIX_ANNOTATION + "-1");
    }

    public static void sendLastPlayedCard(List<Player> allPlayers, Player cardOwner, Card card) {
        List<Player> copy = new ArrayList<>(allPlayers);
        copy.remove(cardOwner);
        copy.forEach(other -> other.send(
                PREFIX_PLAYED_CARD + cardOwner.getUsername() + SPLIT + card.toString()
        ));
    }

    public static Map<String, String> getTokenInfo(String token) throws IOException {
        if (token == null || token.equals("-1") || token.equals("-2")) {
            return null;
        }
        Map<String, String> tokenInfo = new HashMap<>();

        String linkCheckFull = WEB_CHECK_URL + "?token=" + token;

        URL url = new URL(linkCheckFull);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine = br.readLine();

            if (inputLine.contains(",")) {
                String split1[] = inputLine.split(",");
                for (String str1 : split1) {
                    if (str1.contains(":")) {
                        String split2[] = str1.split(":");
                        tokenInfo.put(split2[0], split2[1]);
                    } else {
                        tokenInfo.put(str1, str1);
                    }
                }
            }
        }

        return tokenInfo;
    }

    public static String requestToken(String username, String password) throws IOException {
        String loginLink = WEB_LOGIN_URL + "?username=" + username + "&password=" + password;

        URL url = new URL(loginLink);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String token = br.readLine();
        br.close();
        return token;

    }

}
