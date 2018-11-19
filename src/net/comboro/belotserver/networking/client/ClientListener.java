package net.comboro.belotserver.networking.client;

import net.comboro.belotserver.networking.SerializableMessage;

import java.io.IOException;

public interface ClientListener {

    void onReceive(SerializableMessage<?> message);

    void onConnect();

    void onDisconnect();

    void onConnectionError(IOException io);

    class ClientAdapter implements ClientListener {

        @Override
        public void onReceive(SerializableMessage<?> message) {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public void onConnect() {
        }

        @Override
        public void onConnectionError(IOException io) {
        }
    }
}