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
    public JCheckBox confedentialityCheck;
    public JCheckBox integrityCheck;
    public JCheckBox authenticationCheck;
    public JButton okayButton;

    private StyledDocument doc;
    private Style statusStyle;
    private Style messageStyle;
    private Style clientStyle;
    private Style serverStyle;

    private String title;

    public ChatWindow(String title) {
        this.title = title;

        doc = chatTextArea.getStyledDocument();
        statusStyle = chatTextArea.addStyle("Status Style", null);
        StyleConstants.setForeground(statusStyle, Color.GRAY);
        messageStyle = chatTextArea.addStyle("Message Style", null);
        StyleConstants.setForeground(messageStyle, Color.BLACK);
        clientStyle= chatTextArea.addStyle("Client Style", null);
        StyleConstants.setForeground(clientStyle, Color.MAGENTA);
        serverStyle = chatTextArea.addStyle("Server Style", null);
        StyleConstants.setForeground(serverStyle, Color.CYAN);
    }

    public void showStatus(String message) {
        appendMessage(message, statusStyle, true);
    }

    public void showMessage(String from, String message) {
        Style nameStyle = serverStyle;
        if (from.compareTo("client") == 0) {
            nameStyle = clientStyle;
        }

        appendMessage("[ " + from + " ] ", nameStyle, true);
        appendMessage(message, messageStyle, false);
    }

    public void clearInput() {
        chatInput.setText("");
    }

    public void lockChecks() {
        confedentialityCheck.setEnabled(false);
        integrityCheck.setEnabled(false);
        authenticationCheck.setEnabled(false);
        okayButton.setVisible(false);
    }

    private void appendMessage(String message, Style style, boolean newline) {
        try {
            doc.insertString(doc.getLength(), newline ? "\n" + message : message, style);
        } catch (BadLocationException e) {
        }
    }
}
