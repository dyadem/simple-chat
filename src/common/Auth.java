package common;

public class Auth {

    /*
     * Compare the password the user entered to a password hash
     * stored in a file.
     *
     * Return true if passwords match
     */
    public static boolean userLogin(String password) {
        return password.compareTo("password") == 0;
    }

    /*
     * Encrypt the message with AES symmetric encryption
     *
     * TODO: may need to pass the key here
     */
    public static String encryptMessage(String message) {
        return message;
    }

    /*
     * Decrypt the message with AES symmetric encryption
     *
     * TODO: may need to pass the key here
     */
    public static String decryptMessage(String message) {
        return message;
    }

    /*
     * Sign the message with the receivers public key
     *
     * TODO: may need to pass the public key here
     */
    public static String signMessageWithPublicKey(String message) {
        return message;
    }

    /*
     * Verify the message using your private key
     *
     * TODO: may need to pass the private key here
     */
    public static String verifyMessageWithPrivateKey(String message) {
        return message;
    }
}
