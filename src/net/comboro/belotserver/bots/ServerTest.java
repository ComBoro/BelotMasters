package net.comboro.belotserver.bots;

import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.networking.server.BelotServer;
import net.comboro.belotserver.networking.server.ServerListener;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerTest {

    public static void main(String[] args) {
        BelotServer belotServer = new BelotServer(47047);
        belotServer.addLister(new ServerListener.ServerAdapter() {
            @Override
            public void onServerStartError(Exception e) {
                e.printStackTrace();
            }
        });
        belotServer.startServer();

        List<BelotClient> bots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            try {
                Socket clientSocket = new Socket(InetAddress.getByName("localhost"), 47047);
                BelotBot belotBot = new BelotBot(clientSocket, i + 1);
                bots.add(belotBot);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
