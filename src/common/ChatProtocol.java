package common;

import static common.ChatProtocol.State.INIT;
import static common.ChatProtocol.Status.OKAY;
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
        SENT_HELLO,
        SENT_SETTINGS
    }

    private State state;
    private ChatSettings chatSettings;

    public ChatProtocol(ChatSettings chatSettings) {
        this.chatSettings = chatSettings;
        this.state = INIT;
    }

    public String init() {
        return "hello";
    }

    public Status nextMessage(String message) {
        return SUCCEED;
    }

}
