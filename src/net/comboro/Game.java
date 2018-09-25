package net.comboro;

import net.comboro.belotbasics.Card;
import net.comboro.belotbasics.CardUtils;
import net.comboro.belotbasics.Colour;
import net.comboro.belotbasics.Type;
import net.comboro.networking.internet.tcp.ClientTCP;
import net.comboro.team.Team;
import net.comboro.team.Teams;

import java.io.Serializable;
import java.util.*;

import static net.comboro.NetworkStringConstants.*;

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

    private List<Player> playerList = new ArrayList<>();
    private LinkedList<Player> orderedPlayerList = new LinkedList<>();

    private Thread gameThread;

    private int playerStartingFirst;
    private int roundID = -1;

    private int gameMode = GAME_MODE_NOTHING, multiplier = MULTIPLIER_SINGLE;

    private int hangingPoints = 0;

    private boolean started = false, winner = false;
    private Team winnerTeam = null;

    private Teams teams;

    private Player playerToMove;

    Game() {
        teams = new Teams();
    }

    public void addPlayer(String username, ClientTCP client) {
        if (started) return;
        if (username.equals(Application.team1pl1) || username.equals(Application.team1pl2)) {
            playerList.add(new Player(username, client, 1));
        } else playerList.add(new Player(username, client, 2));

        if (playerList.size() == 4) {
            // Sort into teams
            int temp = 0;
            ListIterator<Player> listIterator = playerList.listIterator();
            while (listIterator.hasNext()) {
                Player next = listIterator.next();
                if (next.getTeamId() == (temp % 2) + 1) {
                    orderedPlayerList.add(next);
                } else {
                    listIterator.add(next);
                }
            }

            System.out.println("playerList size: " + playerList.size());
            System.out.println("orderedPlayerList size: " + orderedPlayerList.size());
            System.out.println("temp : " + temp);

            playerStartingFirst = new Random().nextInt() % 4;

            startGame();
        }
    }

    private void sendTeammates() {
        for (Player player : playerList) {
            List<Player> copy = new ArrayList<>(playerList);
            copy.remove(player);
            NetworkUtils.sendTeam(player, teams);
            for (Player other : copy) {
                NetworkUtils.sendTeamOther(player, other, teams);
            }
        }
    }

    private void deal(ListIterator<Card> deckIterator, int amountOfDeals) {
        for (int i = 0; i < 4 * amountOfDeals; i++) {
            playerList.get(i / 3).addCard(deckIterator.next());
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
        int consecutivePasses = -1;

        while (consecutivePasses != 3) {
            for (Player player : playerList) {

                // No more possible annotation
                if (gameMode == GAME_MODE_ALL_TRUMP && multiplier == 4) {
                    break;
                }

                // No possible annotation
                if (teams.getTeam(player).isTrickHolder() && gameMode == GAME_MODE_ALL_TRUMP) {
                    consecutivePasses++;
                    continue;
                }

                String annot = NetworkUtils.sendAnnotaionRequest(player);
                if (annot.startsWith(PREFIX_ANNOTATION)) {
                    int annot_int = Integer.parseInt(annot.substring(PREFIX_ANNOTATION.length()));
                    if (annot_int > gameMode) {
                        consecutivePasses++;

                        // Change the game mode
                        gameMode = annot_int;
                        // Reset multiplier
                        multiplier = MULTIPLIER_SINGLE;
                        // Set team
                        teams.getTeam(player).setTrickHolder(true);
                    } else {
                        consecutivePasses = 0;
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
        for (Player player : playerList) {
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
        return orderedPlayerList.get(index);
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
        Card lastPlayedCard = playerToMove.waitForCard();
        playedCards.add(lastPlayedCard);
        NetworkUtils.sendLastPlayedCard(playerList, playerToMove, lastPlayedCard);

        int arrPos = orderedPlayerList.indexOf(playerToMove);
        for (int i = 0; i < 3; i++) {
            // Get player and card
            playerToMove = orderedPlayerList.get((arrPos + i) % orderedPlayerList.size());
            lastPlayedCard = playerToMove.waitForCard(playedCards, gameMode);

            //TODO: Check for belot

            // Send to all and and to array
            playedCards.add(lastPlayedCard);
            NetworkUtils.sendLastPlayedCard(playerList, playerToMove, lastPlayedCard);
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

            sendTeammates();

            //Rounds(Раздавания)
            while (!winner) {
                sendAll(ROUND_START);
                ListIterator<Card> deckIterator = randomDeckListIterator();
                firstDeal(deckIterator);

                manageBidding();

                if (gameMode == GAME_MODE_NOTHING) {
                    continue;
                }

                secondDeal(deckIterator);

                setTrumps();

                //Tricks

                playerToMove = orderedPlayerList.get(playerStartingFirst);
                for (roundID = 1; roundID < 9; roundID++) {
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
        for (Player player : playerList) player.send(message);
    }

    private <M extends Serializable> void sendOthers(M message, Player sender) {
        for (Player player : playerList) if (!playerList.equals(sender)) player.send(message);
    }

    private ListIterator<Card> randomDeckListIterator() {
        return getRandomDeck().listIterator();
    }

    private List<Card> getRandomDeck() {
        List<Card> deck = Arrays.asList(
                new Card(Colour.Clubs, Type.Seven),
                new Card(Colour.Clubs, Type.Eight),
                new Card(Colour.Clubs, Type.Nine),
                new Card(Colour.Clubs, Type.Ten),
                new Card(Colour.Clubs, Type.Ace),
                new Card(Colour.Clubs, Type.Jack),
                new Card(Colour.Clubs, Type.Queen),
                new Card(Colour.Clubs, Type.King),

                new Card(Colour.Diamonds, Type.Seven),
                new Card(Colour.Diamonds, Type.Eight),
                new Card(Colour.Diamonds, Type.Nine),
                new Card(Colour.Diamonds, Type.Ten),
                new Card(Colour.Diamonds, Type.Ace),
                new Card(Colour.Diamonds, Type.Jack),
                new Card(Colour.Diamonds, Type.Queen),
                new Card(Colour.Diamonds, Type.King),

                new Card(Colour.Hearts, Type.Seven),
                new Card(Colour.Hearts, Type.Eight),
                new Card(Colour.Hearts, Type.Nine),
                new Card(Colour.Hearts, Type.Ten),
                new Card(Colour.Hearts, Type.Ace),
                new Card(Colour.Hearts, Type.Jack),
                new Card(Colour.Hearts, Type.Queen),
                new Card(Colour.Hearts, Type.King),

                new Card(Colour.Spades, Type.Seven),
                new Card(Colour.Spades, Type.Eight),
                new Card(Colour.Spades, Type.Nine),
                new Card(Colour.Spades, Type.Ten),
                new Card(Colour.Spades, Type.Ace),
                new Card(Colour.Spades, Type.Jack),
                new Card(Colour.Spades, Type.Queen),
                new Card(Colour.Spades, Type.King)
        );
        Collections.shuffle(deck);
        return deck;
    }

    public boolean hasWinner() {
        return winnerTeam != null;
    }

    public Team getWinnerTeam() {
        return winnerTeam;
    }

}
