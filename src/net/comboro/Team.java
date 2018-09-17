package net.comboro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Team {

    public static Team TEAM_1 = new Team(1), TEAM_2 = new Team(2);
    private final int TEAM_ID;
    private List<Player> players = new ArrayList<>();
    private int overallPoints, trickPoints;
    private boolean trickHolder = false;

    private Team(int id) {
        TEAM_ID = id;
        trickPoints = 0;
        overallPoints = 0;
    }

    public static void forEach(Consumer<Team> action) {
        Arrays.asList(TEAM_1, TEAM_2).forEach(action);
    }

    public static Team getTrickHolder() {
        return TEAM_1.trickHolder ? TEAM_1 : TEAM_2;
    }

    public static Team getById(int id) {
        return id == 1 ? TEAM_1 : TEAM_2;
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

    public Team getOtherTeam() {
        return TEAM_ID == 1 ? TEAM_1 : TEAM_2;
    }

    public List<Declaration> getTeamDeclarations() {
        List<Declaration> teamDecl = new ArrayList<>(players.get(0).getDeclarations());
        teamDecl.addAll(players.get(1).getDeclarations());
        return teamDecl;
    }
}
