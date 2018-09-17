package net.comboro.networking.internet.tcp;

import net.comboro.networking.internet.InternetServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP extends InternetServer<ClientTCP> {

    private ServerSocket serverSocket;

    private int backlog = 50;
    private InetAddress bindAddr = null;

    public ServerTCP(int port) {
        super(port);
    }

    public ServerTCP(int port, int backlog, InetAddress bindAddr) {
        super(port);
        this.backlog = backlog;
        this.bindAddr = bindAddr;
    }

    @Override
    protected void notifyClientRemoval(ClientTCP client) {
        client.preRemoval();
    }

    @Override
    protected void start() throws IOException {
        serverSocket = new ServerSocket(port, backlog, bindAddr);
        acceptClients();
    }

    private void acceptClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientTCP clientTCP = new ClientTCP(true, socket);
                addClient(clientTCP);
            } catch (Throwable e) {
                continue;
            }
        }
    }

    @Override
    protected void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }

    @Override
    public int getPort() {
        if (serverSocket == null) return port;
        return serverSocket.getLocalPort();
    }

}
