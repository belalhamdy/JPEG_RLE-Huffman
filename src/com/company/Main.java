package com.company;

import javafx.util.Pair;

import javax.swing.*;
import java.util.*;

public class Main {
    public static Pair<Integer,Integer> EOB = new Pair<>(0,0);
    public static void main(String[] args) {
        String lecTest = "-2,0,0,2,0,0,3,2,0,1,0,0,-2,0,-1,0,0,1,0,0,-1";
        String lecTestD = "10,01 00,10 00,11 10,10 010,1 00,01 010,0 11,1 11,0 011";

        Map<String,Pair<Integer,Integer>> dict = new LinkedHashMap<>();
        dict.put("10",new Pair<>(0,2));
        dict.put("010",new Pair<>(1,1));
        dict.put("11",new Pair<>(2,1));
        dict.put("00",new Pair<>(2,2));
        dict.put("011",new Pair<>(0,0));
        String lecTestD2 = "1001001000111010010100010100111110011";

        System.out.println(JPEG_RLE.Decompress(lecTestD2,dict));
        Huffman.PrintTree(System.out);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new GUI();
    }
}
