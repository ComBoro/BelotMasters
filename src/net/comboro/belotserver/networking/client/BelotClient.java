package net.comboro.belotserver.networking.client;

import net.comboro.belotserver.networking.SerializableMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;

public class BelotClient {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private Thread thread;

    private Deque<SerializableMessage> sendQueue = new ArrayDeque<>();

    private List<ClientListener> listeners = new ArrayList<>();

    public BelotClient(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());

        this.receive();
    }

    public <M extends Serializable> void send(M message) {
        this.send(new SerializableMessage<>(message));
    }

    public void send(SerializableMessage<?> message) {
        if (sendQueue == null) {
            sendQueue = new ArrayDeque<>();
        }
        sendQueue.offerFirst(message);
        try {
            if (Objects.nonNull(outputStream)) {
                while (!sendQueue.isEmpty()) {
                    outputStream.writeObject(sendQueue.pop());
                }
            }
        } catch (IOException e) {
            fireConnectionError(e);
        }

    }

    private void receive() {
        thread = new Thread(() -> {
            this.thread.setName(socket.getInetAddress().getHostAddress());

            while (!(thread.isInterrupted() || socket.isInputShutdown() || socket.isClosed())) {
                try {
                    SerializableMessage<?> message = (SerializableMessage<?>) inputStream.readObject();
                    fireReceiveEvent(message);
                } catch (IOException | ClassNotFoundException | ClassCastException e) {
                    if (e instanceof IOException) fireConnectionError((IOException) e);
                }
            }
        });
        thread.setName(socket.getInetAddress().getHostAddress());
        thread.start();
    }

    public boolean addListener(ClientListener listener) {
        return listeners.add(listener);
    }

    public boolean removeListener(ClientListener listener) {
        return listeners.remove(listener);
    }

    public void fireDisconnectEvent() {
        listeners.forEach(e -> e.onDisconnect());
    }

    public void fireConnectEvent() {
        listeners.forEach(e -> e.onConnect());
    }

    public void fireConnectionError(IOException io) {
        listeners.forEach(e -> e.onConnectionError(io));
    }

    public void fireReceiveEvent(SerializableMessage<?> message) {
        for (ClientListener cl : new ArrayList<>(listeners)) {
            cl.onReceive(message);
        }
    }

    public void setThreadName(String name) {
        thread.setName(name);
    }

    public void closeConnection() {
        if (thread != null)
            thread.interrupt();
        try {
            socket.shutdownInput();
            socket.close();
        } catch (IOException | NullPointerException e) {

        }

        socket = null;
        inputStream = null;
        outputStream = null;

        thread.interrupt();

        listeners.clear();
    }


}
