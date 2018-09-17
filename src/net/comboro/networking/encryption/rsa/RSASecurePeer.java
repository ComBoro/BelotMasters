package net.comboro.networking.encryption.rsa;

import net.comboro.networking.SerializableMessage;

public interface RSASecurePeer {

    RSAInformation getRSAInformation();

    SerializableMessage<?> decryptRSA(RSASecureMessage message);

}
