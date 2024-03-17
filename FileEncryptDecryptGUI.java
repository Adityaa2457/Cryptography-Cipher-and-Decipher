package cipher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileEncryptDecryptGUI extends JFrame implements ActionListener {
    private JButton selectFileButton, encryptButton, decryptButton, keyButton;
    private JComboBox<String> algorithmComboBox;
    private JTextArea textArea;
    private JTextField keyField;
    private String selectedAlgorithm;
    private String selectedFilePath;

    public FileEncryptDecryptGUI() {
        setTitle("File Encryptor and Decryptor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(this);

        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this);

        decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(this);

        algorithmComboBox = new JComboBox<>(new String[]{"Mono Alphabetic Cipher", "Caesar Cipher", "Hill Cipher", "Playfair Cipher", "Rail Fence Cipher"});
        algorithmComboBox.addActionListener(this);

        keyButton = new JButton("Enter Key");
        keyButton.addActionListener(this);

        keyField = new JTextField(10);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectFileButton);
        buttonPanel.add(algorithmComboBox);
        buttonPanel.add(keyButton);
        buttonPanel.add(keyField);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFilePath = selectedFile.getPath();
                try {
                    List<String> lines = Files.readAllLines(Paths.get(selectedFilePath), StandardCharsets.UTF_8);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String line : lines) {
                        stringBuilder.append(line).append("\n");
                    }
                    textArea.setText(stringBuilder.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == encryptButton) {
            String key = keyField.getText();
            if (selectedAlgorithm != null && selectedFilePath != null && !key.isEmpty()) {
                try {
                    String encryptedText = CryptoUtil.encrypt(selectedAlgorithm, key, textArea.getText());
                    updateFile(selectedFilePath, encryptedText);
                    textArea.setText(encryptedText);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file, algorithm, and provide a key.");
            }
        } else if (e.getSource() == decryptButton) {
            String key = keyField.getText();
            if (selectedAlgorithm != null && selectedFilePath != null && !key.isEmpty()) {
                try {
                    String decryptedText = CryptoUtil.decrypt(selectedAlgorithm, key, textArea.getText());
                    updateFile(selectedFilePath, decryptedText);
                    textArea.setText(decryptedText);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file, algorithm, and provide a key.");
            }
        } else if (e.getSource() == algorithmComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) e.getSource();
            selectedAlgorithm = (String) comboBox.getSelectedItem();
        } else if (e.getSource() == keyButton) {
            String key = JOptionPane.showInputDialog("Enter Key:");
            keyField.setText(key);
        }
    }

    private void updateFile(String filePath, String newText) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(newText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileEncryptDecryptGUI::new);
    }
}