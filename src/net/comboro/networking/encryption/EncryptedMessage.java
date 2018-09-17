package net.comboro.networking.encryption;

import net.comboro.networking.SerializableMessage;

import java.math.BigInteger;

abstract public class EncryptedMessage extends SerializableMessage<String> {
    private static final long serialVersionUID = -7503286664312507506L;

    protected transient BigInteger number;

    public BigInteger getNumber() {
        return new BigInteger(data);
    }

    public byte[] toByteArray() {
        return new BigInteger(data).toByteArray();
    }
}
