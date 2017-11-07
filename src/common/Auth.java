package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
     */
    public static Message encryptMessage(Message message, DiffieHellman diffieHellman) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, diffieHellman.getAesKey());

            byte[] cipherText = cipher.doFinal(message.getBytes());
            message.setBytes(cipherText);
            message.setEncodedParams(cipher.getParameters().getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /*
     * Decrypt the message with AES symmetric encryption
     */
    public static Message decryptMessage(Message message, DiffieHellman diffieHellman) {

        try {
            AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
            aesParams.init(message.getEncodedParams());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, diffieHellman.getAesKey(), aesParams);
            byte[] recovered = cipher.doFinal(message.getBytes());
            message.setBytes(recovered);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    /*
     * Sign the message with the receivers public key
     *
     * TODO: may need to pass the public key here
     */
    public static Message signMessageWithPublicKey(Message message) {
        return message;
    }

    /*
     * Verify the message using your private key
     *
     * TODO: may need to pass the private key here
     */
    public static Message verifyMessageWithPrivateKey(Message message) {
        return message;
    }
}
