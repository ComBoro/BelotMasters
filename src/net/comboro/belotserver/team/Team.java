package net.comboro.belotserver.team;

import net.comboro.belotserver.Declaration;
import net.comboro.belotserver.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final int TEAM_ID;
    private List<Player> players = new ArrayList<>();
    private int overallPoints, trickPoints;
    boolean trickHolder = false;

    Team(int id) {
        TEAM_ID = id;
        trickPoints = 0;
        overallPoints = 0;
    }

    public void addPlayer(Player player) {
        players.add(player);

    }

    public void addToOverallPoints(int points) {
        this.overallPoints += points;
    }

    public void resetOverallPoints() {
        this.overallPoints = 0;
    }

    public int getOverallPoints() {
        return overallPoints;
    }

    public void addToTrickPoints(int points) {
        this.trickPoints += points;
    }

    public void resetTrickPoints() {
        this.trickPoints = 0;
    }

    public int getTrickPoints() {
        return trickPoints;
    }

    public boolean isTrickHolder() {
        return trickHolder;
    }

    public void setTrickHolder(boolean trickHolder) {
        this.trickHolder = trickHolder;
    }

    public int getTeamId() {
        return TEAM_ID;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public List<Declaration> getTeamDeclarations() {
        List<Declaration> teamDecl = new ArrayList<>(players.get(0).getDeclarations());
        teamDecl.addAll(players.get(1).getDeclarations());
        return teamDecl;
    }
}
