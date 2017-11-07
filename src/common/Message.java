package common;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Message implements Serializable {

    private byte[] bytes;

    public Message(String text) {
        this.bytes = text.getBytes();
    }

    public Message(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getText() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
