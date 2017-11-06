package common;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

import static common.ChatWindow.StatusLevel.*;

public class ChatWindow {
    public enum StatusLevel {
        INFO,
        WARN,
        ERROR,
        IMPORTANT
    }

    public JTextField chatInput;
    private JTextPane chatTextArea;
    public JPanel view;
    public JCheckBox confedentialityCheck;
    public JCheckBox integrityCheck;
    public JCheckBox authenticationCheck;
    public JButton okayButton;

    private StyledDocument doc;

    // Status
    private Style infoStyle;
    private Style warnStyle;
    private Style errorStyle;
    private Style importantStyle;

    // Messages
    private Style messageStyle;
    private Style clientStyle;
    private Style serverStyle;

    private String title;

    public ChatWindow(String title) {
        this.title = title;

        doc = chatTextArea.getStyledDocument();

        // Styles
        infoStyle = chatTextArea.addStyle("Info Style", null);
        StyleConstants.setForeground(infoStyle, Color.GRAY);

        warnStyle = chatTextArea.addStyle("Warn Style", null);
        StyleConstants.setForeground(warnStyle, Color.ORANGE);

        errorStyle = chatTextArea.addStyle("Error Style", null);
        StyleConstants.setForeground(errorStyle, Color.RED);

        importantStyle = chatTextArea.addStyle("Important Style", null);
        StyleConstants.setForeground(importantStyle, Color.BLACK);
        StyleConstants.setFontSize(importantStyle, 18);

        messageStyle = chatTextArea.addStyle("Message Style", null);
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        clientStyle= chatTextArea.addStyle("Client Style", null);
        StyleConstants.setForeground(clientStyle, Color.MAGENTA);

        serverStyle = chatTextArea.addStyle("Server Style", null);
        StyleConstants.setForeground(serverStyle, Color.CYAN);
    }

    public void showStatus(String message, StatusLevel level) {
        Style style = null;
        switch (level) {
            case INFO:
                style = infoStyle;
                break;
            case WARN:
                style = warnStyle;
                break;
            case ERROR:
                style = errorStyle;
                break;
            case IMPORTANT:
                style = importantStyle;
                break;
        }

        appendMessage(message, style, true);
    }

    public void showInfo(String message) {
        showStatus(message, INFO);
    }

    public void showWarning(String message) {
        showStatus(message, WARN);
    }

    public void showError(String message) {
        showStatus(message, ERROR);
    }

    public void showImportant(String message) {
        showStatus(message, IMPORTANT);
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
