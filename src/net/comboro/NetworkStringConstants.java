package net.comboro;

public interface NetworkStringConstants {

    String SPLIT = ":";

    String ROUND_START = "round" + SPLIT + "start";

    String PREFIX_PLAYER = "player" + SPLIT;
    String PREFIX_TEAM = "team" + SPLIT;
    String PREFIX_ANNOTATION = "annotation" + SPLIT;
    String PREFIX_MULTIPLIER = "multiplier" + SPLIT;
    String PLAYED_CARD_PREFIX = "playedCard" + SPLIT;

    String MULTIPLIER_DOUBLE = PREFIX_MULTIPLIER + "double";
    String MULTIPLIER_REDOUBLE = PREFIX_MULTIPLIER + "redouble";


    int WAIT_TIME_PLAYER = 25;

}
