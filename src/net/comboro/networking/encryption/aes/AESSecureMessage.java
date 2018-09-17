package net.comboro.networking.encryption.aes;

import net.comboro.networking.SerializableMessage;
import net.comboro.networking.Serializer;
import net.comboro.networking.encryption.EncryptedMessage;

import java.math.BigInteger;

public class AESSecureMessage extends EncryptedMessage {
    private static final long serialVersionUID = -5401068399965401997L;

    public AESSecureMessage(AES aes, SerializableMessage<?> message) {
        byte[] data = Serializer.serialize(message);
        byte[] encryptedData = aes.doFinalEn(data);
        number = new BigInteger(encryptedData);
        this.data = number.toString();
    }

}
