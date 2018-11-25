package net.comboro.belotserver.team;

import net.comboro.belotserver.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Teams {

    public Team TEAM_1 = new Team(1), TEAM_2 = new Team(2);
    public final Team[] teams = new Team[]{TEAM_1, TEAM_2};

    private List<Player> playerList = new ArrayList<>();

    public void rotate(int amount) {
        while (amount-- > 0) rotate();
    }

    public void orderPlayers() {
        playerList.clear();

        List<Player> t1 = TEAM_1.getPlayers(), t2 = TEAM_2.getPlayers();

        playerList.add(0, t1.get(0));
        playerList.add(1, t2.get(0));
        playerList.add(2, t1.get(1));
        playerList.add(3, t2.get(1));
    }

    public void rotate() {
        int lastIndex = playerList.size() - 1;
        Player last = playerList.get(lastIndex);
        playerList.set(lastIndex, playerList.get(0));
        playerList.set(0, last);
    }

    public void addPlayer(Player player, int teamId) {
        getById(teamId).addPlayer(player);
        playerList.add(player);
    }

    public void forEach(Consumer<Team> action) {
        Arrays.asList(TEAM_1, TEAM_2).forEach(action);
    }

    public Team getTrickHolder() {
        return TEAM_1.trickHolder ? TEAM_1 : TEAM_2;
    }

    private Team getById(int id) {
        return id == 1 ? TEAM_1 : TEAM_2;
    }

    public Team getTeam(Player player) {
        return TEAM_1.getPlayers().contains(player) ? TEAM_1 : TEAM_2;
    }

    public Team getOtherTeam(Team team) {
        return (team == TEAM_1) ? TEAM_2 : TEAM_1;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Player getPlayerAt(int index) {
        return playerList.get(index);
    }

    public int indexOf(Player player) {
        return playerList.indexOf(player);
    }
}
