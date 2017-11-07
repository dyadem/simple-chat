package common;

import com.sun.xml.internal.rngom.parse.host.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Base64;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;


public class Auth {

    public static final String SALT = "my360salt";

    /*
     * Compare the password the user entered to a password hash
     * stored in a file.
     *
     * Return true if passwords match
     */
    public static boolean userLogin(String name, String password) throws IOException {
        Boolean isAuthenticated = false;

        try {
            String storedHash = new String(Files.readAllBytes(Paths.get(name + ".txt")));
            String saltedPassword = SALT + password;
            String hashedPassword = generateHash(saltedPassword);
            if (hashedPassword.equals(storedHash)) {
                isAuthenticated = true;
            } else {
                isAuthenticated = false;
            }
        }
        catch (IOException e) {
            System.out.println("" +
                    "login failed: " + e.getMessage());
        }
        return isAuthenticated;
    }


    public static String generateHash(String input) {
        StringBuilder hash = new StringBuilder();

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f' };
            for (int idx = 0; idx < hashedBytes.length; ++idx) {
                byte b = hashedBytes[idx];
                hash.append(digits[(b & 0xf0) >> 4]);
                hash.append(digits[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("" +
                    "Hash algorithm failed: " + e.getMessage());
        }

        return hash.toString();
    }

    /*
     * Encrypt the message with AES symmetric encryption
     *
     * TODO: may need to pass the key here
     */
    public static Message encryptMessage(Message message) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return message;
    }

    /*
     * Decrypt the message with AES symmetric encryption
     *
     * TODO: may need to pass the key here
     */
    public static Message decryptMessage(Message message) {
        return message;
    }




    /*
     * Sign the message with the receivers public key
     *
     * TODO: hash the text before sign
     */
    public static Message signMessageWithPrivateKey(Message message) {
        try {
            Signature signature = Signature.getInstance("DSA");
            signature.initSign(Chat.getPriKey());    // getPrivateKey
            signature.update(message.getText().getBytes());
            byte[] sig = signature.sign();    // sign hashedText with privateKey
            Base64.Encoder encoder = Base64.getEncoder();
            message.setSignature(encoder.encodeToString(sig));
        } catch (Exception e) {
            System.out.println("Failed to sign the message.");
        } return message;
    }

    /*
     * Verify the message using your private key
     */
    public static boolean verifyMessageWithPublicKey(Message message) {
        try {
            Signature ver = Signature.getInstance("DSA");
            ver.initVerify(Chat.getPubKey());    // getPublicKey
            ver.update(message.getText().getBytes());
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = decoder.decode(message.getSignature());    // get message.getSignature
            ver.verify(bytes);    // verify it with publicKey
        } catch (Exception e) {
            System.out.println("Message verification failed.");
        } return true;
    }

}