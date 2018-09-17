package net.comboro.networking;

import net.comboro.networking.encryption.aes.AESInformation;
import net.comboro.networking.encryption.aes.AESSecureMessage;
import net.comboro.networking.encryption.rsa.RSA;
import net.comboro.networking.encryption.rsa.RSAInformation;
import net.comboro.networking.encryption.rsa.RSASecureMessage;
import net.comboro.networking.encryption.rsa.RSASecurePeer;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server<T extends Client> {

    private final Object lock = new Object();
    protected RSASecurePeer securePeer = null;
    protected RSA rsa = null;
    protected ExecutorService serverExecutor;
    protected boolean hasTrouble = false;
    protected Exception lastException = null;
    protected List<T> clientList = new ArrayList<>();
    protected List<ServerListener<T>> serverListeners = new ArrayList<>();
    private boolean active = false, secure = false;

    protected Server() {
        serverExecutor = Executors.newSingleThreadExecutor();
        setSecure(true);
    }

    @Deprecated
    protected Executor getExecutor() {
        return serverExecutor;
    }

    public void sendAll(SerializableMessage<Serializable> message) {
        clientList.forEach(e -> e.send(message));
    }

    public <M extends Serializable> void sendAll(M message) {
        clientList.parallelStream().forEach(e -> e.send(message));
    }

    protected boolean addClient(T client) {
        boolean result;
        synchronized (lock) {
            result = clientList.add(client);
            serverListeners.forEach(e -> e.onClientConnect(client));
            client.addListener(new Client.ClientListener.ClientAdapter() {
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

            if (secure) {
                RSAInformation rsaInf = securePeer.getRSAInformation();
                SerializableMessage<RSAInformation> rsaMessage = new SerializableMessage<>(rsaInf);
                client.send(rsaMessage);
            }

            lock.notifyAll();
        }
        return result;
    }

    public boolean addLister(ServerListener<T> serverListener) {
        return serverListeners.add(serverListener);
    }

    public void fireClientInputEvent(T client, SerializableMessage<?> message) {

        if (message.getData() instanceof RSAInformation && client.getRSAInformation() == null) {
            RSAInformation clientRSA = (RSAInformation) message.getData();
            RSAInformation serverRSA = securePeer.getRSAInformation();

            //RSA
            client.setRSAInfomation(clientRSA);

            //AES
            SecureRandom secureRandom = new SecureRandom();
            byte[] bytes = new byte[16];
            secureRandom.nextBytes(bytes);

            AESInformation aesInf = new AESInformation(bytes);
            client.setAesInformation(bytes);

            RSASecureMessage sm = new RSASecureMessage(clientRSA, serverRSA, new SerializableMessage<>(aesInf));
            client.send(sm);
            return;
        }

        if (message instanceof AESSecureMessage) {
            AESSecureMessage aesSecureMessage = (AESSecureMessage) message;
            SerializableMessage decrypted = client.getAES().decrypt(aesSecureMessage);
            fireClientInputEvent(client, decrypted);
        } else if (message instanceof RSASecureMessage) {
            RSASecureMessage rsaData = (RSASecureMessage) message;
            SerializableMessage decrypted = client.decryptRSA(rsaData);
            fireClientInputEvent(client, decrypted);
        } else

            serverListeners.forEach(e -> e.onClientInput(client, message));
    }

    public void fireClientDisconnectEvent(T client) {
        serverListeners.forEach(e -> e.onClientDisconnect(client));
    }

    public boolean removeClient(T client) {
        boolean result;
        synchronized (lock) {
            client.fireDisconnectEvent();
            result = clientList.remove(client);
            if (result)
                serverListeners.forEach(e -> e.onClientDisconnect(client));
            lock.notifyAll();
        }
        return result;
    }

    abstract protected void notifyClientRemoval(T client);

    public boolean removeListener(ServerListener<T> listener) {
        return serverListeners.remove(listener);
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        if (isActive())
            throw new IllegalArgumentException("Server is active");
        if (!isSecure())
            makeSecure();
        this.secure = secure;
    }

    private void makeSecure() {
        rsa = new RSA();

        securePeer = new RSASecurePeer() {

            @Override
            public RSAInformation getRSAInformation() {
                return Server.this.rsa.getInformation();
            }

            @Override
            public SerializableMessage<?> decryptRSA(RSASecureMessage message) {
                BigInteger en = message.getNumber();
                BigInteger de = rsa.decrypt(en);
                byte[] data = de.toByteArray();
                return Serializer.deserialize(data);
            }
        };
    }

    public void startServer() {
        active = true;
        serverExecutor.execute(() -> {
            try {
                serverListeners.forEach(e -> e.onServerStart());
                start();
            } catch (Exception e) {
                hasTrouble = true;
                lastException = e;
                active = false;
                serverListeners.forEach(e1 -> e1.onServerStartError(e));
            }
        });
    }

    public void stopServer() {
        active = false;
        serverExecutor.execute(() -> stop());
        serverExecutor.shutdown();
        serverListeners.forEach(e -> e.onServerStop());
        serverListeners.clear();
        serverExecutor.shutdownNow();
    }

    abstract protected void start() throws Exception;

    abstract protected void stop();

    public boolean hasTrouble() {
        return Boolean.valueOf(hasTrouble);
    }

    public boolean isActive() {
        return Boolean.valueOf(active);
    }

    public List<T> getClientList() {
        return new ArrayList<>(clientList);
    }

    public interface ServerListener<T extends Client> {

        void onServerStart();

        void onServerStartError(Exception e);

        void onServerStop();

        void onClientConnect(T client);

        void onClientInput(T client, SerializableMessage<?> message);

        void onClientDisconnect(T client);

        class ServerAdapter<T extends Client> implements ServerListener<T> {

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
            public void onClientConnect(T client) {
            }

            @Override
            public void onClientInput(T client, SerializableMessage<?> message) {
            }

            @Override
            public void onClientDisconnect(T client) {
            }
        }
    }

}
