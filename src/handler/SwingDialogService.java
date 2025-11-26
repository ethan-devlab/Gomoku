package handler;

import javax.swing.*;

/**
 * Default implementation of DialogService using Swing JOptionPane.
 */
public class SwingDialogService implements DialogService {

    @Override
    public void showInfo(String title, String message) {
        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE));
    }

    @Override
    public void showWarning(String title, String message) {
        SwingUtilities
                .invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE));
    }

    @Override
    public void showError(String title, String message) {
        SwingUtilities
                .invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
    }

    @Override
    public boolean showConfirm(String title, String message) {
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
