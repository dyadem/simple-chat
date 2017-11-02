package common;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatWindow {
    public JTextField chatInput;
    private JTextPane chatTextArea;
    public JPanel view;

    private StyledDocument doc;
    private Style statusStyle;
    private Style messageStyle;
    private Style nameStyle;

    private String title;

    public ChatWindow(String title) {
        this.title = title;

        doc = chatTextArea.getStyledDocument();
        statusStyle = chatTextArea.addStyle("Status Style", null);
        StyleConstants.setForeground(statusStyle, Color.GRAY);
        messageStyle = chatTextArea.addStyle("Message Style", null);
        StyleConstants.setForeground(messageStyle, Color.BLACK);
        nameStyle = chatTextArea.addStyle("Name Style", null);
        StyleConstants.setForeground(nameStyle, Color.MAGENTA);
    }

    public void showStatus(String message) {
        appendMessage(message, statusStyle, true);
    }

    public void showMessage(String name, String message) {
        appendMessage("[ " + name + " ] ", nameStyle, true);
        appendMessage(message, messageStyle, false);
    }

    private void appendMessage(String message, Style style, boolean newline) {
        try {
            doc.insertString(doc.getLength(), newline ? "\n" + message : message, style);
        } catch (BadLocationException e) {}
    }
}
