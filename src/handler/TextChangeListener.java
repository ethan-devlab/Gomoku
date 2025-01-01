package handler;

import ui.InitialUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextChangeListener implements DocumentListener {
    private final InitialUI initUI;
    private final JTextField pTextF;
    private final JButton startButton;

    public TextChangeListener(InitialUI initUI, JButton startButton, JTextField p1TextF) {
        this.initUI = initUI;
        this.startButton = startButton;
        this.pTextF = p1TextF;
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
        String pName = pTextF.getText().trim();
        boolean isConnected = initUI.getClientState();

        startButton.setEnabled(isConnected && !pName.isEmpty());
    }

}
