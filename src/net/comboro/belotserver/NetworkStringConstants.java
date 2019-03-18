package net.comboro.belotserver;

public interface NetworkStringConstants {

    String WEB_CHECK_URL = "http://krustevi.myqnapcloud.com/tokeninfo.php";
    String WEB_LOGIN_URL = "http://krustevi.myqnapcloud.com/login.php";

    String SPLIT = ":";

    String ROUND_START = "round" + SPLIT + "start";
    String ROUND_GAMEMODE = "round" + SPLIT + "gamemode" + SPLIT;

    String TRICK_START = "trick" + "start";

    String PREFIX_PLAYER = "player" + SPLIT;
    String PREFIX_TEAM = "team" + SPLIT;
    String PREFIX_MULTIPLIER = "multiplier" + SPLIT;

    // START ANNOTATION
    String PREFIX_ANNOTATION = "annotation" + SPLIT;

    String ANNOTATION_TIME = "time" + SPLIT;
    String ANNOTATION_PLAYER = "player" + SPLIT;

    String PREFIX_ANNOTATION_TIME = PREFIX_ANNOTATION + ANNOTATION_TIME;
    String PREFIX_ANNOTATION_PLAYER = PREFIX_ANNOTATION + ANNOTATION_PLAYER;
    // END ANNOTATION

    // Start CARD
    String PREFIX_CARD = "card" + SPLIT;

    String ADD_CARD = "add" + SPLIT;
    String PLAYED_CARD = "played" + SPLIT;
    String PLAY_CARD = "play" + SPLIT;
    String TIME_FOR_CARD = "time" + SPLIT;


    String PREFIX_ADD_CARD = PREFIX_CARD + ADD_CARD; //<CARD>
    String PREFIX_PLAYED_CARD = PREFIX_CARD + PLAYED_CARD; // <PLAYER>:<CARD>
    String PREFIX_PLAY_CARD = PREFIX_CARD + PLAY_CARD; // CARD
    String PREFIX_TIME_FOR_CARD = PREFIX_CARD + TIME_FOR_CARD; //<VALUE>
    // End CARD

    String MULTIPLIER_DOUBLE = PREFIX_MULTIPLIER + "double";
    String MULTIPLIER_REDOUBLE = PREFIX_MULTIPLIER + "redouble";

    int WAIT_TIME_PLAYER = 25;

}
