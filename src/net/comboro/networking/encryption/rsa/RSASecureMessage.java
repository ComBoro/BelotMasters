package net.comboro.networking.encryption.rsa;

import net.comboro.networking.SerializableMessage;
import net.comboro.networking.Serializer;
import net.comboro.networking.encryption.EncryptedMessage;

import java.math.BigInteger;

public class RSASecureMessage extends EncryptedMessage {
    private static final long serialVersionUID = 892224637500521515L;

    public RSAInformation senderRSA;

    public RSASecureMessage(RSAInformation receiverRSA, RSAInformation senderRSA, SerializableMessage<?> message) {
        this.senderRSA = senderRSA;
        number = new BigInteger(Serializer.serialize(message));
        number = number.negate();
        number = RSA.encrypt(number, receiverRSA);
        this.data = number.toString();
    }

}
