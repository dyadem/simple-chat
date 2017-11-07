package common;

import java.io.Serializable;

public class Message implements Serializable {

    private String text;
    private String signature;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getSignature() { return signature; }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
