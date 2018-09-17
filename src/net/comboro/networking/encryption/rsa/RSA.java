package net.comboro.networking.encryption.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.probablePrime;

public class RSA {

    public static final int defaultBytes = 2048;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final BigInteger modulus;
    private final BigInteger e = new BigInteger("65537"); // public key (2^16 + 1)
    private final BigInteger d; // private key

    public RSA() {
        this(defaultBytes);
    }

    public RSA(int bytes) {
        BigInteger p = probablePrime(bytes / 2, secureRandom);
        BigInteger q = probablePrime(bytes / 2, secureRandom);
        modulus = p.multiply(q);
        BigInteger toitent = p.subtract(ONE).multiply(q.subtract(ONE));
        d = e.modInverse(toitent);
    }

    static public BigInteger encrypt(BigInteger toEncrypt, BigInteger e, BigInteger modulus) {
        return toEncrypt.modPow(e, modulus);
    }

    static public BigInteger encrypt(BigInteger toEncrypt, RSAInformation information) {
        return encrypt(toEncrypt, information.getPublicKey(), information.getModulus());
    }

    public BigInteger encrypt(BigInteger toEncrypt) {
        return toEncrypt.modPow(e, modulus);
    }

    public BigInteger decrypt(BigInteger toDecrypt) {
        return toDecrypt.modPow(d, modulus);
    }

    public RSAInformation getInformation() {
        return new RSAInformation(e, modulus);
    }

    @Override
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != RSA.class))
            return false;
        RSA other = (RSA) obj;
        return modulus.equals(other.modulus);
    }
}
