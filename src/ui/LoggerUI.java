package ui;

import handler.ExportHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoggerUI extends JFrame {
    private JPanel panel1;
    private JScrollPane scrollPane;
    private JTextArea logArea;
    private JPanel FuncPanel;
    private JButton exportButton;

    ExportHandler exportHandler;

    private ArrayList<String> dataList;


    public LoggerUI() {
//        logArea.setEditable(false);
        Font font = new Font("Arial", Font.BOLD, 13);
        logArea.setFont(font);
        init();
    }

    public void appendText(ArrayList<String> dataList) {
        this.dataList = dataList;
        for (String data : dataList) {
            logArea.append(data + "\n");
        }
    }

    public void clear() {
        logArea.setText("");
    }

    public ArrayList<String> getDataList() {
        return this.dataList;
    }

    private void init() {
        setTitle("Gomoku Game Logger");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // make center
        exportHandler = new ExportHandler(this);
        exportButton.addActionListener(exportHandler);
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(LoggerUI::new);
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        scrollPane = new JScrollPane();
        panel1.add(scrollPane, BorderLayout.CENTER);
        logArea = new JTextArea();
        logArea.setText("");
        scrollPane.setViewportView(logArea);
        FuncPanel = new JPanel();
        FuncPanel.setLayout(new GridBagLayout());
        panel1.add(FuncPanel, BorderLayout.SOUTH);
        exportButton = new JButton();
        exportButton.setText("Export");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        FuncPanel.add(exportButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        FuncPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        FuncPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        FuncPanel.add(spacer3, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
