package net.comboro.team;

import net.comboro.Player;

import java.util.Arrays;
import java.util.function.Consumer;

public class Teams {

    private Team TEAM_1 = new Team(1), TEAM_2 = new Team(2);

    public void forEach(Consumer<Team> action) {
        Arrays.asList(TEAM_1, TEAM_2).forEach(action);
    }

    public Team getTrickHolder() {
        return TEAM_1.trickHolder ? TEAM_1 : TEAM_2;
    }

    public Team getById(int id) {
        return id == 1 ? TEAM_1 : TEAM_2;
    }

    public Team getTeam(Player player) {
        return getById(player.getTeamId());
    }

}
