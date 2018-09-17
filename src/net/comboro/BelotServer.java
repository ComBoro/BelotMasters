package net.comboro;

import net.comboro.networking.SerializableMessage;
import net.comboro.networking.internet.tcp.ClientTCP;
import net.comboro.networking.internet.tcp.ServerTCP;

import java.util.Map;

public class BelotServer extends ServerTCP {

    private ServerListener.ServerAdapter<ClientTCP> adapter = new ServerListener.ServerAdapter<>() {
        @Override
        public void onClientDisconnect(ClientTCP client) {

        }

        @Override
        public void onClientInput(ClientTCP client, SerializableMessage message) {
            if (message.getData() instanceof String) {
                String str = (String) message.getData();

                // Registering
                if (str.startsWith("reg:")) {
                    String us_pass = str.substring(4);
                    String split[] = us_pass.split(",");
                    String username = split[0], password = split[1];

                    for (Map.Entry<String, String> entry : Application.expectedPlayers.entrySet()) {
                        if (entry.getKey().equals(username) && entry.getValue().equals(password)) {
                            //TODO Game.addPlayer(username, client);
                        } else {
                            client.send("Wrong credentials.");
                            removeClient(client);
                        }
                    }
                }
            }
        }
    };

    public BelotServer(int port) {
        super(port);
        addLister(adapter);
    }

}
