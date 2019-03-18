package net.comboro.belotserver.bots;

import networking.client.BelotClient;

import java.net.InetAddress;
import java.net.Socket;

public class ClientTest {

    public static void main(String[] args) throws Throwable {
        Socket socket = new Socket(InetAddress.getLocalHost(), 47247);
        BelotClient client = new BelotClient(socket);
        client.send("qj mi kura");
    }

}
