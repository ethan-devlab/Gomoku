package handler;

import ui.LoggerUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

public class ExportHandler implements ActionListener {

    private final LoggerUI loggerUI;

    public ExportHandler(LoggerUI loggerUI) {
        this.loggerUI = loggerUI;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt");
        fileChooser.addChoosableFileFilter(filter);
        int res = fileChooser.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION)
        {
            String filename = fileChooser.getSelectedFile().getAbsolutePath() + filter.getDescription();
            try (PrintWriter logger = new PrintWriter(filename)) {
                this.loggerUI.getDataList().forEach(logger::println);
                logger.println("\nDATA SAVED AT " + ZonedDateTime.now());
                System.out.println("Data saved to " + filename);
                JOptionPane.showMessageDialog(null,
                        "Data saved to " + filename,
                        "Export", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (FileNotFoundException ex) {
                System.out.println("Save error: " + ex.getMessage());
                JOptionPane.showMessageDialog(null,
                        "Save error: " + ex.getMessage(),
                        "Export", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
