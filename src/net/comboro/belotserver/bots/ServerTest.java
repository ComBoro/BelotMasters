package net.comboro.belotserver.bots;

import net.comboro.belotserver.Game;
import net.comboro.belotserver.networking.SerializableMessage;
import networking.Token;
import networking.client.BelotClient;
import networking.server.BelotServer;
import networking.server.ServerListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerTest {

    private static Game game = new Game();

    public static void main(String[] args) {
        BelotServer belotServer = new BelotServer(47247);
        belotServer.addLister(new ServerListener.ServerAdapter() {
            @Override
            public void onClientInput(BelotClient client, SerializableMessage message) {
                if (message.getData() instanceof String) {
                    String str = (String) message.getData();
                    System.out.println(str);
                    //Login
                    if (str.startsWith("token:") || str.startsWith("login:")) {
                        str = str.substring(6);

                        try {
                            Token token = new Token(str);
                            game.addPlayer(token, client);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            client.send(e.getMessage());
                            belotServer.removeClient(client);
                        }
                    }
                }
            }

            @Override
            public void onClientDisconnect(BelotClient client) {
                System.out.println("A client disconnected.");
            }

            @Override
            public void onServerStartError(Exception e) {
                e.printStackTrace();
            }
        });

        if (args.length > 0) {
            int bot_amount = Integer.parseInt(args[0]);

            belotServer.startServer();


            List<BelotBot> bots = new ArrayList<>();
            for (int i = 0; i < bot_amount; i++) {
                try {
                    Socket clientSocket = new Socket(InetAddress.getByName("localhost"), 47247);
                    BelotBot belotBot = new BelotBot(clientSocket, i + 1);
                    bots.add(belotBot);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
