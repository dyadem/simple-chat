package common;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;


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
     * Use the key generated by Diffie Hellman
     */
    public static Message encryptMessage(Message message, DiffieHellman diffieHellman) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, diffieHellman.getAesKey());

            // plaintext -> ciphertext
            byte[] cipherText = cipher.doFinal(message.getBytes());
            System.out.println("Encrypted Text: " + Utils.toHexString(cipherText));
            message.setBytes(cipherText);
            message.setEncodedParams(cipher.getParameters().getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /*
     * Decrypt the message with AES symmetric encryption
     *
     * Use the key generated by Diffie Hellman
     */
    public static Message decryptMessage(Message message, DiffieHellman diffieHellman) {
        try {
            AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
            aesParams.init(message.getEncodedParams());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, diffieHellman.getAesKey(), aesParams);

            // ciphertext -> plaintext
            byte[] recovered = cipher.doFinal(message.getBytes());
            message.setBytes(recovered);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    /*
     * Sign the message with the senders private key
     */
    public static Message signMessageWithPrivateKey(Message message) {
        try {
            Signature signature = Signature.getInstance("DSA");
            signature.initSign(Chat.getPriKey());
            signature.update(message.getText().getBytes());
            byte[] sig = signature.sign();

            System.out.println("Signature: " + Utils.toHexString(sig));

            Base64.Encoder encoder = Base64.getEncoder();
            message.setSignature(encoder.encodeToString(sig));
        } catch (Exception e) {
            System.out.println("Failed to sign the message.");
            System.out.println(e.getMessage());
        }
        return message;
    }

    /*
     * Verify the message using the public key
     */
    public static boolean verifyMessageWithPublicKey(Message message) {
        try {
            Signature ver = Signature.getInstance("DSA");
            ver.initVerify(Chat.getPubKey());
            ver.update(message.getText().getBytes());
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = decoder.decode(message.getSignature());    // get message.getSignature
            ver.verify(bytes);    // verify it with publicKey
        } catch (Exception e) {
            System.out.println("Message verification failed.");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

}