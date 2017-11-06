package common;

public class Auth {

    public static boolean userLogin(String password) {
        return password.compareTo("password") == 0;
    }

    public static String encryptMessage(String message) {
        return message;
    }

    public static String decryptMessage(String message) {
        return message;
    }

    public static String signMessageWithPublicKey(String message) {
        return message;
    }

    public static String unSignMessageWithPrivateKey(String message) {
        return message;
    }
}
