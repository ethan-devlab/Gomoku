package handler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PortChangeListener implements DocumentListener {
    private final JFormattedTextField portTextF;
    private final JButton startServer;
    private final ServerHandler serverHandler;

    public PortChangeListener(JFormattedTextField portTextF, JButton startServer, ServerHandler serverHandler) {
        this.portTextF = portTextF;
        this.startServer = startServer;
        this.serverHandler = serverHandler;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkEnabled();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkEnabled();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        checkEnabled();
    }

    private void checkEnabled() {
        String text = portTextF.getText().trim();
        if (!text.isEmpty() && text.matches("\\d+")){
            try {
                int port = Integer.parseInt(text);
                serverHandler.setPORT(port);
                startServer.setEnabled(port > 1023 && port <= 65535);
            } catch (NumberFormatException ex) {
                System.out.println("Wrong input: " + ex.getMessage());
            }
        }
    }
}
