package com.company;

import javax.swing.*;
import java.awt.*;

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

    GUI(){
        JFrame form = new JFrame("JPEG RLE Huffman");
        form.setMaximumSize(new Dimension(700, 800));
        form.setPreferredSize(new Dimension(600, 500));
        form.setResizable(true);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setContentPane(mainpnl);
        form.pack();
        form.setVisible(true);
        form.setLocationRelativeTo(null);
    }
}
