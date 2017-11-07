package common;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellman {

    private KeyPair keyPair;
    private KeyAgreement keyAgreement;
    private PublicKey senderPublicKey;
    private byte[] secret;

    public DiffieHellman(KeyPair keyPair, KeyAgreement keyAgreement) {
        this.keyPair = keyPair;
        this.keyAgreement = keyAgreement;
    }

    public byte[] getEncodedPublicKey() {
        return keyPair.getPublic().getEncoded();
    }

    public void setSenderPublicKey(byte[] senderPubKeyEnc) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(senderPubKeyEnc);
        this.senderPublicKey = keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public void doDHPhase() throws Exception {
        System.out.println("Alice Execute PHASE1");
        keyAgreement.doPhase(senderPublicKey, true);
    }

    public void generateSharedSecret() {
        byte[] sharedSecret = keyAgreement.generateSecret();
        System.out.println("\nShared Secret");
        System.out.println(new String(sharedSecret, StandardCharsets.UTF_8));
        this.secret = sharedSecret;
    }

    public byte[] getSecret() {
        return secret;
    }

    public static DiffieHellman generateFreshKey() throws Exception {
        // Create DH key pair with 2048 bit key size
        System.out.println("Alice Generate DH keypair");
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Create DH KeyAgreement object
        System.out.println("Alice Init");
        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());

        // Encode public key
        byte[] pubKeyEnc = keyPair.getPublic().getEncoded();

        return new DiffieHellman(keyPair, keyAgree);
    }

    public static DiffieHellman generateFromPublic(byte[] senderPubKeyEnc) throws Exception {
        // Convert encoded public key into object
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(senderPubKeyEnc);

        PublicKey senderPublicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // Get DH parameters associated with senders public key
        DHParameterSpec dhParamFromPublicKey = ((DHPublicKey)senderPublicKey).getParams();

        // Create key pair using DH parameters
        System.out.println("Bob Generate DH keypair");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(dhParamFromPublicKey);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Create and init DH KeyAgreement object
        System.out.println("Bob Init");
        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());

        // Encode public key
        byte[] pubKeyEnc = keyPair.getPublic().getEncoded();

        DiffieHellman dh = new DiffieHellman(keyPair, keyAgree);
        dh.setSenderPublicKey(senderPubKeyEnc);
        return dh;
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }
}
