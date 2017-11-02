package common;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow {
    public JTextField chatInput;
    private JTextPane chatTextArea;
    public JPanel view;

    private String title;

    public ChatWindow(String title) {
        this.title = title;
    }

    public void showStatus(String message) {
        String text = chatTextArea.getText();
        chatTextArea.setText(text + "\n" + message);
    }

    public void showMessage(String name, String message) {
        String newText = "[ " + name + " ] " + message;
        chatTextArea.setText(chatTextArea.getText() + "\n" + newText);
    }
}
