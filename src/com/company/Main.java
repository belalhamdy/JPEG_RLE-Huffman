package com.company;

import javafx.util.Pair;

import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new GUI();
    }
}
