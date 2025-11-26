package handler;

/**
 * Interface for displaying dialogs to the user.
 * Abstracts JOptionPane calls for testability.
 */
public interface DialogService {

    /**
     * Show an informational message.
     */
    void showInfo(String title, String message);

    /**
     * Show a warning message.
     */
    void showWarning(String title, String message);

    /**
     * Show an error message.
     */
    void showError(String title, String message);

    /**
     * Show a confirmation dialog.
     * 
     * @return true if user confirms, false otherwise
     */
    boolean showConfirm(String title, String message);
}
