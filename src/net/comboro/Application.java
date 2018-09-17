package net.comboro;

import net.comboro.networking.Server;
import net.comboro.networking.internet.tcp.ClientTCP;

import java.util.HashMap;

public class Application {

    static int port;
    static HashMap<String, String> expectedPlayers = new HashMap<>();
    static String team1pl1, team1pl2;

    static BelotServer belotServer;

    public static void main(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            expectedPlayers.put(args[1], args[2]);
            expectedPlayers.put(args[3], args[4]);
            expectedPlayers.put(args[5], args[6]);
            expectedPlayers.put(args[7], args[8]);

            int teamWithPl1 = Integer.parseInt(args[9]);
            team1pl1 = args[1];
            team1pl2 = args[2 * teamWithPl1 - 1];
        } catch (Exception e) {
            System.err.println("Invalid arguments, server shutting down...");
            Runtime.getRuntime().exit(1);
        }

        belotServer = new BelotServer(port);
        belotServer.addLister(new Server.ServerListener.ServerAdapter<ClientTCP>() {
            @Override
            public void onServerStartError(Exception e) {
                System.err.println("Error starting server");
                Runtime.getRuntime().exit(2);
            }
        });
        belotServer.startServer();
    }
}
