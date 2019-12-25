package com.company;

import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GUI {
    private JPanel panel1;
    private JPanel mainpnl;
    private JTabbedPane tabbedPane1;
    private JButton compressButton;
    private JButton decompressButton;
    private JTextField fromtxt;
    private JButton frombtn;
    private JTextField totxt;
    private JButton tobtn;
    private JButton compressFilebtn;
    private JButton decompressFilebtn;
    private JTextArea decompresstxt;
    private JTextArea compresstxt;

    GUI() {
        JFrame form = new JFrame("JPEG RLE Huffman");
        form.setMaximumSize(new Dimension(700, 800));
        form.setPreferredSize(new Dimension(600, 500));
        form.setResizable(true);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setContentPane(mainpnl);
        form.pack();
        form.setVisible(true);
        form.setLocationRelativeTo(null);

        compressFilebtn.addActionListener(e -> {
            try {
                String text = readAllFromFile(fromtxt.getText());
                String result = compressButtonPressed(text);
                saveAllToFile(totxt.getText(), result);
                Huffman.PrintTree(System.out);
            } catch (Exception ex) {
                INVALID();
            }
        });
        compressButton.addActionListener(e -> {
            try {
                String text = compresstxt.getText();
                String result = compressButtonPressed(text);
                decompresstxt.setText(result);
                Huffman.PrintTree(System.out);
            } catch (Exception ex) {
                INVALID();
            }
        });
        decompressFilebtn.addActionListener(e -> {
            try {
                String text = readAllFromFile(fromtxt.getText());
                String result = decompressButtonPressed(text);
                saveAllToFile(totxt.getText(), result);
            } catch (Exception ex) {
                INVALID();
            }
        });
        decompressButton.addActionListener(e -> {
            try {
                String text = decompresstxt.getText();
                String result = decompressButtonPressed(text);
                compresstxt.setText(result);
            } catch (Exception ex) {
                INVALID();
            }
        });
        frombtn.addActionListener(e -> fromtxt.setText(selectFile()));
        tobtn.addActionListener(e -> totxt.setText(selectFile()));
    }

    static void INVALID() {
        JOptionPane.showMessageDialog(null, "Error in inputs", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String selectFile() {
        JFileChooser jf = new JFileChooser("D:\\");
        if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return jf.getSelectedFile().getPath();
        }
        else return "";
    }

    static void saveAllToFile(String path, String data) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(path);
        out.println(data.trim());
        out.close();
    }

    static String readAllFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    static String compressButtonPressed(String text) {
        return JPEG_RLE.Compress(text);
    }

    static String decompressButtonPressed(String text) {
        return JPEG_RLE.Decompress(text);
    }
}
