package net.comboro.belotserver;

import net.comboro.belotserver.networking.server.BelotServer;
import net.comboro.belotserver.networking.server.ServerListener;

import java.util.HashMap;

public class Application {

    private int port;
    private HashMap<String, String> expectedPlayers = new HashMap<>(); //<Username, Password>

    private BelotServer belotServer;

    private Application(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            /*
            expectedPlayers.put(args[1], args[2]);
            expectedPlayers.put(args[3], args[4]);
            expectedPlayers.put(args[5], args[6]);
            expectedPlayers.put(args[7], args[8]);


            int teamWithPl1 = Integer.parseInt(args[9]);
            if(teamWithPl1 < 2 || teamWithPl1 > 4) throw new IllegalArgumentException("Invalid team 1 player id.");
            expectedPlayers.put("team1p1", args[1]);
            expectedPlayers.put("team1pl2", args[2*(teamWithPl1-2)+3]);

            System.out.println("Server started. Team 1:" + expectedPlayers.get("team1p1") + ", " + expectedPlayers.get("team1pl2"));
            */

            System.out.println("Server started on port " + args[0]);
        } catch (Exception e) {
            System.err.println("Invalid arguments, server shutting down...");
            Runtime.getRuntime().exit(1);
        }

        belotServer = new BelotServer(port);
        belotServer.addLister(new ServerListener.ServerAdapter() {
            @Override
            public void onServerStartError(Exception e) {
                System.err.println("Error starting server");
                Runtime.getRuntime().exit(2);
            }
        });
        belotServer.startServer();
    }

    public static void main(String[] args) {
        new Application(args);
    }
}
