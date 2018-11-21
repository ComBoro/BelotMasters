package net.comboro.belotserver.networking.server;

import net.comboro.belotserver.Game;
import net.comboro.belotserver.networking.SerializableMessage;
import net.comboro.belotserver.networking.Token;
import net.comboro.belotserver.networking.client.BelotClient;
import net.comboro.belotserver.networking.client.ClientListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BelotServer {

    private final Object lock = new Object();
    private ServerSocket serverSocket;
    private int port;
    private ExecutorService serverExecutor;
    private List<BelotClient> clientList = new ArrayList<>();
    private List<ServerListener> serverListeners = new ArrayList<>();

    private Game game;

    public BelotServer(int port) {
        this.port = port;
        serverExecutor = Executors.newSingleThreadExecutor();
        addLister(getAdapter());
        game = new Game();
    }

    private ServerListener.ServerAdapter getAdapter() {
        return new ServerListener.ServerAdapter() {
            @Override
            public void onClientInput(BelotClient client, SerializableMessage message) {
                if (message.getData() instanceof String) {
                    String str = (String) message.getData();

                    //System.out.println(str);

                    //Login
                    if (str.startsWith("token:") || str.startsWith("login:")) {
                        str = str.substring(6);

                        //noinspection finally
                        try {
                            Token token = new Token(str);
                            game.addPlayer(token, client);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            client.send(e.getMessage());
                            removeClient(client);
                        }
                    }
                }
            }
        };
    }

    private void acceptClients() {
        while (!serverSocket.isClosed()) try {
            Socket socket = serverSocket.accept();
            BelotClient belotClient = new BelotClient(socket);
            addClient(belotClient);
        } catch (Exception e) {
        }
    }

    private void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean addClient(BelotClient client) {
        boolean result;
        synchronized (lock) {
            result = clientList.add(client);
            serverListeners.forEach(e -> e.onClientConnect(client));
            client.addListener(new ClientListener.ClientAdapter() {
                @Override
                public void onReceive(SerializableMessage<?> message) {
                    fireClientInputEvent(client, message);
                }

                @Override
                public void onConnectionError(java.io.IOException io) {
                    removeClient(client);
                }
            });
            client.fireConnectEvent();

            lock.notifyAll();
        }
        return result;
    }

    public boolean addLister(ServerListener serverListener) {
        return serverListeners.add(serverListener);
    }

    public void fireClientInputEvent(BelotClient client, SerializableMessage<?> message) {
        serverListeners.forEach(e -> e.onClientInput(client, message));
    }

    public void fireClientDisconnectEvent(BelotClient client) {
        serverListeners.forEach(e -> e.onClientDisconnect(client));
    }

    public boolean removeClient(BelotClient client) {
        boolean result;
        synchronized (lock) {
            client.fireDisconnectEvent();
            result = clientList.remove(client);
            if (result)
                fireClientDisconnectEvent(client);
            lock.notifyAll();
        }
        return result;
    }

    public boolean removeListener(ServerListener listener) {
        return serverListeners.remove(listener);
    }

    public void startServer() {
        serverExecutor.execute(() -> {
            try {
                serverListeners.forEach(ServerListener::onServerStart);
                serverSocket = new ServerSocket(port);
                acceptClients();
            } catch (Exception e) {
                serverListeners.forEach(e1 -> e1.onServerStartError(e));
            }
        });
    }

    public void stopServer() {
        serverExecutor.execute(this::stop);
        serverExecutor.shutdown();
        serverListeners.forEach(ServerListener::onServerStop);
        serverListeners.clear();
        serverExecutor.shutdownNow();
    }

    public List<BelotClient> getClientList() {
        return new ArrayList<>(clientList);
    }


}
