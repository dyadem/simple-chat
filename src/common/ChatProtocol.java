package common;

import static common.ChatProtocol.State.*;
import static common.ChatProtocol.Status.OKAY;
import static common.ChatProtocol.Status.REFUSE;
import static common.ChatProtocol.Status.SUCCEED;

public class ChatProtocol {

    // OKAY: continue with protocol
    // SUCCEED: protocol passed
    // REFUSE: protocol failed, reject connection
    public enum Status {
        OKAY,
        SUCCEED,
        REFUSE
    }

    /*

        Client                      Server
        start           -------->
                        <--------   hello
        hello           -------->
                        <--------   chat settings
        chat settings   -------->

     */
    enum State {
        INIT,
        HELLO_SENT,
        SETTINGS_SENT,
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
        this.state = INIT;
    }

    public String init() {
        this.state = HELLO_SENT;
        return "hello";
    }

    public ProtocolResult nextMessage(String message) {
        ProtocolResult result = refuseResult();

        if (message.compareTo("reject") == 0) {
            state = REJECT;
        }

        switch (state) {
            // New message initing protocol
            // Send hello back
            case INIT:
                if (message.compareTo("hello") == 0) {
                    result = new ProtocolResult("hello", OKAY);
                    state = HELLO_SENT;
                    break;
                }

            // Waiting on the chat settings
            case HELLO_SENT:
                if (message.compareTo(chatSettings.getSettingsString()) == 0) {
                    result = new ProtocolResult(chatSettings.getSettingsString(), OKAY);
                    state = SETTINGS_SENT;
                    break;
                } else {
                    result = refuseResult();
                    state = REJECT;
                }

            // At this point we are okay with connectings chat settings
            // Just waiting for goahead on connection
            case SETTINGS_SENT:
                if (message.compareTo("go") == 0) {
                    result = new ProtocolResult("go", SUCCEED);
                    state = ACCEPT;
                    break;
                }

            case REJECT:
                break;
            case ACCEPT:
                result = new ProtocolResult("", SUCCEED);
                break;
        }

        if (result.newStatus == REFUSE) {
            state = REJECT;
        }

        return result;
    }

    private ProtocolResult refuseResult() {
        return new ProtocolResult("", REFUSE);
    }

}
