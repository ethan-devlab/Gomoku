package handler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ClientInfoListener implements DocumentListener {
    private final JTextField addrTextF;
    private final JTextField portTextF;
    private final JButton connectButton;
    private final ClientHandler clientHandler;
    public ClientInfoListener(JTextField addrTextF, JTextField portTextF, JButton connectButton,
                              ClientHandler clientHandler) {
        this.addrTextF = addrTextF;
        this.portTextF = portTextF;
        this.connectButton = connectButton;
        this.clientHandler = clientHandler;
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
        String addrText = addrTextF.getText().trim();
        String portText = portTextF.getText().trim();
        String ipPattern = "^(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";

        if (!addrText.isEmpty() && !portText.isEmpty() &&
            (addrText.matches(ipPattern) || addrText.equals("localhost")) &&
            portText.matches("\\d+")){
            try {
                int port = Integer.parseInt(portText);
                clientHandler.setADDRESS(addrText);
                clientHandler.setPORT(port);
                connectButton.setEnabled(port > 1023 && port <= 65535);
            } catch (NumberFormatException ex) {
                System.out.println("Wrong input: " + ex.getMessage());
            }
        }
        else {
            connectButton.setEnabled(false);
        }
    }
}
