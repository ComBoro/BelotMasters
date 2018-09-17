package net.comboro.networking.encryption.aes;

import net.comboro.networking.SerializableMessage;
import net.comboro.networking.Serializer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AES {

    public final String algorithm = this.getClass().getSimpleName();
    private Cipher cipherEn, cipherDe;
    private boolean trouble = false;

    public AES(byte[] keyVal) {
        Key key = new SecretKeySpec(keyVal, algorithm);
        try {
            cipherEn = Cipher.getInstance(algorithm);
            cipherEn.init(Cipher.ENCRYPT_MODE, key);

            cipherDe = Cipher.getInstance(algorithm);
            cipherDe.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            trouble = true;
        }
    }

    public byte[] doFinalEn(byte[] input) {
        try {
            return cipherEn.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            trouble = true;
            return input;
        }
    }

    public byte[] doFinalDe(byte[] input) {
        try {
            return cipherDe.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            trouble = true;
            return input;
        }
    }

    public AESSecureMessage encrypt(SerializableMessage<?> serializableMessage) {
        return new AESSecureMessage(this, serializableMessage);
    }

    public SerializableMessage<?> decrypt(AESSecureMessage message) {
        byte[] dataEn = message.toByteArray();
        byte[] data = doFinalDe(dataEn);
        return Serializer.deserialize(data);
    }

    public boolean hasTrouble() {
        return trouble;
    }

}
