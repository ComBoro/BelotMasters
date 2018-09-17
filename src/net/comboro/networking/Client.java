package net.comboro.networking;

import net.comboro.networking.encryption.aes.AES;
import net.comboro.networking.encryption.aes.AESInformation;
import net.comboro.networking.encryption.aes.AESSecureMessage;
import net.comboro.networking.encryption.rsa.RSA;
import net.comboro.networking.encryption.rsa.RSAInformation;
import net.comboro.networking.encryption.rsa.RSASecureMessage;
import net.comboro.networking.encryption.rsa.RSASecurePeer;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

abstract public class Client implements RSASecurePeer {

    public RSAInformation rsaInformation = null;
    public AESInformation aesInformation = null;
    protected RSAInformation serverRSA = null;
    protected boolean trouble = false, serverSide;
    protected Exception lastException = null;
    protected List<ClientListener> listeners = new ArrayList<>();
    RSA rsa = null;
    AES aes = null;

    public Client() {
        this(false);
    }

    public Client(boolean serverSide) {
        this.serverSide = serverSide;
        if (!serverSide) {
            rsa = new RSA();
            setRSAInfomation(rsa.getInformation());
            // Send RSA Information
            this.send(getRSAInformation());
        }
    }

    public void sendEncrypted(SerializableMessage<?> message) {
        send(aes.encrypt(message));
    }

    public <M extends Serializable> void sendEncrypted(M message) {
        this.sendEncrypted(new SerializableMessage<>(message));
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

        if (!serverSide) {
            // Decrypt

            if (message instanceof RSASecureMessage) {
                RSASecureMessage sm = (RSASecureMessage) message;
                message = Client.this.decryptRSA(sm);
            } else if (message instanceof AESSecureMessage) {
                AESSecureMessage sm = (AESSecureMessage) message;
                message = aes.decrypt(sm);
            }

            if (message.getData() instanceof RSAInformation) {
                serverRSA = (RSAInformation) message.getData();
                RSAInformation clientRSA = this.getRSAInformation();

                SerializableMessage<RSAInformation> respond = new SerializableMessage<>(clientRSA);
                send(respond);
                return;
            } else if (message.getData() instanceof AESInformation) {
                byte[] key = ((AESInformation) message.getData()).getKey();
                aes = new AES(key);
                aesInformation = new AESInformation(key);
                return;
            }
        }

        for (ClientListener cl : listeners) {
            cl.onReceive(message);
        }
    }

    public void closeConnection() {
        if (serverSide) return;
        try {
            onConnectionTermination();
        } catch (IOException io) {
            trouble = true;
            lastException = io;
        }
    }

    protected abstract void onConnectionTermination() throws IOException;

    public boolean hasTrouble() {
        return Boolean.valueOf(trouble);
    }

    public Exception getLastException() {
        return new Exception(lastException);
    }

    @Override
    public SerializableMessage<?> decryptRSA(RSASecureMessage message) {
        BigInteger en = message.getNumber();
        BigInteger de = rsa.decrypt(en);
        de = de.negate();
        byte[] data = de.toByteArray();
        return Serializer.deserialize(data);
    }

    public AES getAES() {
        return aes;
    }

    @Override
    public RSAInformation getRSAInformation() {
        return rsaInformation;
    }

    public void setRSAInfomation(RSAInformation rsa) {
        this.rsaInformation = rsa;
    }

    public void setAesInformation(byte[] key) {
        this.aesInformation = new AESInformation(key);
        this.aes = new AES(key);
    }

    public AESInformation getAESInformation() {
        return aesInformation;
    }

    public abstract void send(SerializableMessage<?> message);

    public <M extends Serializable> void send(M message) {
        this.send(new SerializableMessage<>(message));
    }

    @Override
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != Client.class))
            return false;
        Client other = (Client) obj;
        return this.rsa.equals(other.rsa);
    }

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
}
