package net.comboro.networking.encryption.aes;

import java.io.Serializable;

public class AESInformation implements Serializable {
    private static final long serialVersionUID = 4436379720962924182L;

    private byte[] key;

    public AESInformation(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

}
