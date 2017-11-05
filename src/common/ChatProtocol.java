package common;

import static common.ChatProtocol.State.*;
import static common.ChatProtocol.Status.OKAY;
import static common.ChatProtocol.Status.REFUSE;
import static common.ChatProtocol.Status.SUCCEED;

public class ChatProtocol {

    private final String hello = "hello";
    private final String go = "go";
    private final String reject = "reject";

    // OKAY: continue with protocol
    // SUCCEED: protocol passed
    // REFUSE: protocol failed, reject connection
    public enum Status {
        OKAY,
        SUCCEED,
        REFUSE
    }

    enum State {
        INIT,
        C_HELLO_SENT,
        C_SETTINGS_SENT,
        C_GO_SENT,
        S_HELLO_SENT,
        S_SETTINGS_SENT,
        ACCEPT,
        REJECT
    }

    public class ProtocolResult {
        public String newMessage;
        public Status newStatus;

        public ProtocolResult(String newMessage, Status newStatus) {
            this.newMessage = newMessage;
            this.newStatus = newStatus;
        }
    }

    private State state;
    private ChatSettings chatSettings;

    public ChatProtocol(ChatSettings chatSettings) {
        this.chatSettings = chatSettings;
        init();
    }

    public void init() {
        state = INIT;
    }

    public String startProtocol() {
        this.state = C_HELLO_SENT;
        return hello;
    }

    public ProtocolResult nextMessage(String message) {
        ProtocolResult result = refuseResult();
        State newState = state;

        if (message.compareTo(reject) == 0) {
            newState = REJECT;
        }

        switch (state) {
            // New message initing protocol to the server
            // Send hello back
            case INIT:
                if (message.compareTo(hello) == 0) {
                    result = new ProtocolResult(hello, OKAY);
                    newState = S_HELLO_SENT;
                    break;
                }

            // The client has sent hello to init, wait on hello back
            // Send chat settings
            case C_HELLO_SENT:
                if (message.compareTo(hello) == 0) {
                    result = new ProtocolResult(chatSettings.getSettingsString(), OKAY);
                    newState = C_SETTINGS_SENT;
                    break;
                } else {
                    result = refuseResult();
                    newState = REJECT;
                }

            // The client has sent the chat settings, wait on them back
            // Send go
            case C_SETTINGS_SENT:
                if (message.compareTo(chatSettings.getSettingsString()) == 0) {
                    result = new ProtocolResult(go, OKAY);
                    newState = C_GO_SENT;
                    break;
                } else {
                    result = refuseResult();
                    newState = REJECT;
                }

            // The client is okay with handshake, wait on go from server
            // If go then protocol has completed successfully
            case C_GO_SENT:
                if (message.compareTo(go) == 0) {
                    result = new ProtocolResult("", SUCCEED);
                    newState = ACCEPT;
                    break;
                }

            // The server is waiting on the chat settings
            // Send settings back
            case S_HELLO_SENT:
                if (message.compareTo(chatSettings.getSettingsString()) == 0) {
                    result = new ProtocolResult(chatSettings.getSettingsString(), OKAY);
                    newState = S_SETTINGS_SENT;
                    break;
                } else {
                    result = refuseResult();
                    newState = REJECT;
                }

            // The server is waiting for a gohead from client
            // Send confirmation go back
            case S_SETTINGS_SENT:
                if (message.compareTo(go) == 0) {
                    result = new ProtocolResult(go, SUCCEED);
                    newState = ACCEPT;
                    break;
                }

            case REJECT:
                break;
            case ACCEPT:
                result = new ProtocolResult("", SUCCEED);
                break;
        }

        if (result.newStatus == REFUSE) {
            newState = REJECT;
        }

        state = newState;
        return result;
    }

    private ProtocolResult refuseResult() {
        return new ProtocolResult(reject, REFUSE);
    }

}
