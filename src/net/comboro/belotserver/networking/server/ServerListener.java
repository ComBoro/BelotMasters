package net.comboro.belotserver.networking.server;

import net.comboro.belotserver.networking.SerializableMessage;
import net.comboro.belotserver.networking.client.BelotClient;

public interface ServerListener {

    void onServerStart();

    void onServerStartError(Exception e);

    void onServerStop();

    void onClientConnect(BelotClient client);

    void onClientInput(BelotClient client, SerializableMessage<?> message);

    void onClientDisconnect(BelotClient client);

    class ServerAdapter implements ServerListener {

        @Override
        public void onServerStart() {
        }

        @Override
        public void onServerStartError(Exception e) {
        }

        @Override
        public void onServerStop() {
        }

        @Override
        public void onClientConnect(BelotClient client) {
        }

        @Override
        public void onClientInput(BelotClient client, SerializableMessage<?> message) {
        }

        @Override
        public void onClientDisconnect(BelotClient client) {
        }
    }
}