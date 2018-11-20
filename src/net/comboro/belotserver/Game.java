package net.comboro.belotserver;

import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.belotbasics.CardUtils;
import net.comboro.belotserver.belotbasics.Colour;
import net.comboro.belotserver.networking.NetworkStringConstants;
import net.comboro.belotserver.networking.NetworkUtils;
import net.comboro.belotserver.networking.Token;
import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.team.Team;
import net.comboro.belotserver.team.Teams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.Collectors;

import static net.comboro.belotserver.networking.NetworkStringConstants.*;

public class Game {

    public static final int WAIT_TIME_PLAYER = 25;

    public static final int
            GAME_MODE_NOTHING = -1,
            GAME_MODE_CLUBS = 0,
            GAME_MODE_DIAMONDS = 1,
            GAME_MODE_HEARTS = 2,
            GAME_MODE_SPADES = 3,
            GAME_MODE_NO_TRUMP = 4,
            GAME_MODE_ALL_TRUMP = 5;

    private static final int
            TOTAL_POINTS_COLOUR_GAME = 162,
            TOTAL_POINTS_NO_TRUMP = 260,
            TOTAL_POINTS_ALL_TRUMP = 258;

    private static final int
            MULTIPLIER_SINGLE = 1,
            MULTIPLIER_DOUBLE = 2,
            MULTIPLIER_REDOUBLE = 4;

    private Thread gameThread;

    private int roundID = -1;

    private int gameMode = GAME_MODE_NOTHING, multiplier = MULTIPLIER_SINGLE;

    private int hangingPoints = 0;

    private boolean started = false, winner = false;
    private Team winnerTeam = null;

    private Teams teams;
    private List<String> team1Names;

    private Player playerToMove;

    private int temp_team = 0;

    Game(List<String> team1Names) {
        this.team1Names = team1Names;
        this.teams = new Teams();
    }

    public Game() {
        this.teams = new Teams();
    }

    public void addPlayer(Token token, BelotClient client) {
        if (started) return;

        if (teams.getPlayerList().stream().map(player -> player.getToken().toString()).collect(Collectors.toList()).contains(token.toString())) {
            System.err.println("Removed duplicated client");
            return;
        }
        //TODO:
        /*
        if (team1Names.contains(username)) {
            Player player = new Player(username, client, 1);
            teams.addPlayer(player,1);
        } else {
            Player player = new Player(username, client, 2);
            teams.addPlayer(player,2);
        } */
        int team = (temp_team++ % 2) + 1;
        Player player = new Player(token, client);
        teams.addPlayer(player, team);

        if (teams.getPlayerList().size() == 4) {
            teams.rotate(new Random().nextInt() % 4); //Random first player
            startGame();
        }
    }

    private void deal(ListIterator<Card> deckIterator, int amountOfDeals) {
        for (int i = 0; i < 4 * amountOfDeals; i++) {
            teams.getPlayerList().get(i % 4).addCard(deckIterator.next());
            deckIterator.remove();
        }
    }

    private void firstDeal(ListIterator<Card> deckIterator) {
        deal(deckIterator, 3);
        deal(deckIterator, 2);
    }

    private void secondDeal(ListIterator<Card> deckIterator) {
        deal(deckIterator, 3);
    }

    private void manageBidding() {
        int consecutivePasses = 0;
        List<Integer> previousAnnots = new ArrayList<>();
        threepassloop:
        while (consecutivePasses < 3) {
            for (Player player : teams.getPlayerList()) {
                if (consecutivePasses > 2) break threepassloop;

                // No more possible annotation
                if (gameMode == GAME_MODE_ALL_TRUMP && multiplier == 4) {
                    break threepassloop;
                }

                // No possible annotation
                if (teams.getTeam(player).isTrickHolder() && gameMode == GAME_MODE_ALL_TRUMP) {
                    consecutivePasses++;
                    continue;
                }

                String annot = NetworkUtils.sendAnnotaionRequest(player, previousAnnots);
                if (annot.startsWith(PREFIX_ANNOTATION)) {
                    int annot_int = Integer.parseInt(annot.substring(PREFIX_ANNOTATION.length()));
                    if (annot_int > gameMode) {
                        consecutivePasses = 0;

                        // Change the game mode
                        gameMode = annot_int;

                        // Add to List
                        previousAnnots.add(gameMode);

                        // Reset multiplier
                        multiplier = MULTIPLIER_SINGLE;
                        // Set team
                        teams.getTeam(player).setTrickHolder(true);
                    } else {
                        consecutivePasses++;
//                        System.out.println("consecutivePasses:" + consecutivePasses);
                    }
                } else if (annot.startsWith(PREFIX_MULTIPLIER)) {
                    if (gameMode != GAME_MODE_NOTHING) {
                        if (annot.equals(NetworkStringConstants.MULTIPLIER_DOUBLE)) {
                            multiplier = MULTIPLIER_DOUBLE;
                        } else if (annot.equals(NetworkStringConstants.MULTIPLIER_REDOUBLE)) {
                            multiplier = MULTIPLIER_REDOUBLE;
                        }
                        // Set team
                        teams.getTeam(player).setTrickHolder(true);
                    }
                }
            }
        }
    }

    private void setTrumps() {
        for (Player player : teams.getPlayerList()) {
            switch (gameMode) {
                case GAME_MODE_CLUBS:
                    player.getCards().stream().filter(c -> c.COLOUR.equals(Colour.Clubs)).forEach(Card::setTrump);
                    break;
                case GAME_MODE_DIAMONDS:
                    player.getCards().stream().filter(c -> c.COLOUR.equals(Colour.Diamonds)).forEach(Card::setTrump);
                    break;
                case GAME_MODE_HEARTS:
                    player.getCards().stream().filter(c -> c.COLOUR.equals(Colour.Hearts)).forEach(Card::setTrump);
                    break;
                case GAME_MODE_SPADES:
                    player.getCards().stream().filter(c -> c.COLOUR.equals(Colour.Spades)).forEach(Card::setTrump);
                    break;
                case GAME_MODE_ALL_TRUMP:
                    player.getCards().forEach(Card::setTrump);
                    break;
            }
        }
    }

    private Player getTrickHolder(List<Card> playedCards) {
        List<Card> playedCardsCopy = new ArrayList<>(playedCards);
        CardUtils.sort(playedCardsCopy);
        Card strongest = playedCardsCopy.get(playedCardsCopy.size() - 1);
        int index = playedCards.indexOf(strongest) % 4;
        return teams.getPlayerAt(index);
    }

    private int getTrickPoints(List<Card> playedCards, int roundID) {
        int trickPoints = playedCards.stream().mapToInt(Card::getValue).sum();

        // Final 10 points
        if (roundID == 8)
            trickPoints += 10;
        return trickPoints;
    }

    private void manageTrick() {
        List<Card> playedCards = new ArrayList<>();

        sendAll(TRICK_START);

        int arrPos = teams.indexOf(playerToMove);

        for (int i = 0; i < 4; i++) {
            // Get player and card
            playerToMove = teams.getPlayerAt((arrPos + i) % teams.getPlayerList().size());

            System.out.println("Player to move: " + playerToMove.getUsername());
            System.out.println("Cards in hand: " + playerToMove.getCards().toString());
            System.out.println("Played cards: " + playedCards.toString());

            Card lastPlayedCard = playerToMove.waitForCard();
            System.out.println("Played card: " + lastPlayedCard.toString());
            System.out.println();

            //TODO: Check for belot

            // Send to all and and to array
            playedCards.add(lastPlayedCard);
            NetworkUtils.sendLastPlayedCard(teams.getPlayerList(), playerToMove, lastPlayedCard);
        }
        playerToMove = getTrickHolder(playedCards);

        int trickPoints = getTrickPoints(playedCards, roundID);

        teams.getTeam(playerToMove).addToTrickPoints(trickPoints);
    }

    private void doubleIfNoTrumps() {
        teams.forEach(team -> team.addToTrickPoints(team.getTrickPoints()));
    }

    private int getGamePoints(int gameMode) {
        if (gameMode > 0 && gameMode < 3) return TOTAL_POINTS_COLOUR_GAME;
        else if (gameMode == 4) return TOTAL_POINTS_NO_TRUMP;
        else return TOTAL_POINTS_ALL_TRUMP;
    }

    private int addDeclarationPoints() {
        if (gameMode != GAME_MODE_NO_TRUMP) {
            Team t1 = teams.TEAM_1, t2 = teams.TEAM_2;

            List<Declaration>
                    declarationsT1 = t1.getTeamDeclarations(),
                    declarationsT2 = t2.getTeamDeclarations();

            Declaration.filterStrongest(declarationsT1, declarationsT2);

            int
                    pointsT1 = declarationsT1.stream().mapToInt(Declaration::getPoints).sum(),
                    pointsT2 = declarationsT2.stream().mapToInt(Declaration::getPoints).sum();

            t1.addToTrickPoints(pointsT1);
            t2.addToTrickPoints(pointsT2);

            return pointsT1 + pointsT2;
        }
        return 0;
    }

    private void managePoints() {
        Team trickHolder = teams.getTrickHolder();
        Team otherTeam = teams.getOtherTeam(teams.getOtherTeam(trickHolder));

        addValatPoints();

        doubleIfNoTrumps();

        int gamePoints = getGamePoints(gameMode);


        int declarationPoints = addDeclarationPoints();

        gamePoints += declarationPoints;
        int overallPoints = (gamePoints + 2) / 10;

        if (otherTeam.getTrickPoints() > trickHolder.getTrickPoints()) {
            // Inside
            otherTeam.addToOverallPoints((gamePoints + 2) / 10);

            otherTeam.addToOverallPoints(hangingPoints);
            hangingPoints = 0;
        } else {
            int otherPoints = otherTeam.getTrickPoints(), otherFinalPoints = otherPoints / 10;
            if (gameMode < GAME_MODE_ALL_TRUMP) {
                otherFinalPoints += otherPoints % 10 > 5 ? 1 : 0;
            } else {
                otherFinalPoints += otherPoints % 10 > 3 ? 1 : 0;
            }

            int overallFinalPoints = overallPoints / 2;
            overallFinalPoints += overallPoints % 2;

            // Hanging points
            if (otherFinalPoints == overallFinalPoints) {
                otherTeam.addToOverallPoints(overallFinalPoints);
                hangingPoints += overallFinalPoints;
            } else { // Nothing is hanging
                trickHolder.addToOverallPoints(overallFinalPoints - otherFinalPoints);
                otherTeam.addToOverallPoints(otherPoints);

                trickHolder.addToOverallPoints(hangingPoints);
                hangingPoints = 0;
            }

        }
    }

    private void manageWinners() {
        for (Team team : teams.teams)
            findWinner(team);
    }

    private void findWinner(Team team) {
        Team other = teams.getOtherTeam(team);
        boolean otherValat = other.getTrickPoints() == 0;
        if (team.getOverallPoints() > 150 && team.getOverallPoints() > other.getOverallPoints() && !otherValat) {
            winner = true;
            winnerTeam = teams.TEAM_1;
        }
    }

    private void addValatPoints() {
        teams.forEach(team ->
                team.addToOverallPoints(
                        teams.getOtherTeam(team).getTrickPoints() == 0 ? 9 : 0
                )
        );
    }

    private void startGame() {
        gameThread = new Thread(() -> {
            started = true;

            System.out.println("Game started");
            System.out.println();

            NetworkUtils.sendTeammates(teams);

            //Rounds(Раздавания)
            while (!winner) {
                sendAll(ROUND_START);

                ListIterator<Card> deckIterator = CardUtils.randomDeckListIterator();
                System.out.println("First Deal");
                firstDeal(deckIterator);

                // Print cards after deal
                teams.getPlayerList().forEach(p -> System.out.println(p.getUsername() + " | " + p.getCards().toString()));
                System.out.println();

                System.out.println("Bidding");
                manageBidding();
                System.out.println("END Bidding");
                System.out.println();

                if (gameMode == GAME_MODE_NOTHING) {
                    continue;
                }

                System.out.println("Game mode : " + gameMode);
                System.out.println();

                sendAll(NetworkStringConstants.ROUND_GAMEMODE + gameMode);

                System.out.println("Second Deal");
                secondDeal(deckIterator);
                // Print cards after deal
                teams.getPlayerList().forEach(p -> System.out.println(p.getUsername() + "| " + p.getCards().toString()));
                System.out.println();

                setTrumps();

                //Tricks

                teams.orderPlayers();

                playerToMove = teams.getPlayerAt(0);
                for (roundID = 1; roundID < 9; roundID++) {
                    System.out.println("Trick ID: " + roundID);
                    manageTrick();
                }

                managePoints();
                manageWinners();
            }

            //TODO: Dispay winner
        });
        gameThread.start();
    }

    private <M extends Serializable> void sendAll(M message) {
        for (Player player : teams.getPlayerList()) player.send(message);
    }

    private <M extends Serializable> void sendOthers(M message, Player sender) {
        List<Player> list = teams.getPlayerList();
        for (Player player : list) if (!list.equals(sender)) player.send(message);
    }

    public boolean hasWinner() {
        return winnerTeam != null;
    }

    public Team getWinnerTeam() {
        return winnerTeam;
    }

}
