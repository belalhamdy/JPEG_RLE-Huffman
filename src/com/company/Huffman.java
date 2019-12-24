package com.company;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.io.PrintStream;
import java.util.*;

public class Huffman {
    private static class Node {
        String tempForTesting;
        Pair<Integer, Integer> symbol;
        Node left, right;
        int freq;

        Node(Pair<Integer, Integer> symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
            tempForTesting = "";
        }

        @Override
        public String toString() {
            if (this.symbol == null) return tempForTesting;
            return this.symbol.getKey() + " / " + this.symbol.getValue();
        }
    }

    static Map<Pair<Integer, Integer>, Integer> freqs;
    static Map<Pair<Integer, Integer>, String> codes;

    static Node root;

    public static String encode(List<Pair<Integer, Integer>> data) {
        init();
        fillFrequencies(data);
        buildTree();

        StringBuilder ret = new StringBuilder();
        for(Pair<Integer, Integer> current : data){
            ret.append(codes.get(current)).append(",");
        }
        return ret.toString();
    }
    public static List<Pair<Integer,Integer>> decode(List<String> data, Map<String,Pair<Integer,Integer>> dict){
        List<Pair<Integer,Integer>> ret = new ArrayList<>();
        for(String curr : data){
            ret.add(dict.getOrDefault(curr,null));
        }
        return ret;
    }
    private static void init() {
        freqs = new HashMap<>();
        codes = new HashMap<>();
    }

    private static void fillFrequencies(List<Pair<Integer, Integer>> data) {
        for (Pair<Integer, Integer> curr : data) {
            int f = freqs.getOrDefault(curr, 0) + 1;
            freqs.put(curr, f);
        }
    }

    private static PriorityQueue<Node> getQueue() {
        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> {
            if (o1.freq == o2.freq) return 1; // to give the second higher probability
            return Integer.compare(o1.freq, o2.freq);
        });
        for (Map.Entry<Pair<Integer, Integer>, Integer> entry : freqs.entrySet())
            queue.add(new Node(entry.getKey(), entry.getValue()));
        return queue;
    }

    private static void buildTree() {
        Node left, right;
        PriorityQueue<Node> queue = getQueue();

        while (queue.size() > 1) {
            right = queue.poll();
            left = queue.poll();

            Node temp = new Node(null, left.freq + right.freq);
            temp.tempForTesting = left.symbol + " " + right.symbol;

            temp.left = left;
            temp.right = right;

            queue.add(temp);
        }
        root = queue.poll();
        assignCodes(root, "");
    }

    private static void assignCodes(Node curr, String code) {
        if (curr.symbol != null) {
            if (code.isEmpty()) codes.put(curr.symbol, "1"); // default for root if the tree contains only root
            else codes.put(curr.symbol, code);
            return;
        }
        if (curr.left != null) assignCodes(curr.left, code + "0");
        if (curr.right != null) assignCodes(curr.right, code + "1");
    }
    public static void PrintTree(@NotNull PrintStream out) {
        if (root.left == null && root.right == null) return;
        out.println();
        List<List<String>> lines = new ArrayList<>();
        List<Node> level = new ArrayList<>();
        List<Node> next = new ArrayList<>();

        level.add(root);
        int nn = 1;

        int widest = 0;

        while (nn != 0) {
            List<String> line = new ArrayList<>();

            nn = 0;

            for (Node n : level) {
                if (n == null) {
                    line.add(null);

                    next.add(null);
                    next.add(null);
                } else {
                    String aa = n.toString();
                    line.add(aa);
                    if (aa.length() > widest) widest = aa.length();

                    next.add(n.left);
                    next.add(n.right);

                    if (n.left != null) nn++;
                    if (n.right != null) nn++;
                }
            }

            if (widest % 2 == 1) widest++;

            lines.add(line);

            List<Node> tmp = level;
            level = next;
            next = tmp;
            next.clear();
        }

        int perpiece = lines.get(lines.size() - 1).size() * (widest);
        for (int i = 0; i < lines.size(); i++) {
            List<String> line = lines.get(i);
            int hpw = (int) Math.floor(perpiece / 2f) - 1;

            if (i > 0) {
                for (int j = 0; j < line.size(); j++) {

                    // split node
                    char c = ' ';
                    if (j % 2 == 1) {
                        if (line.get(j - 1) != null) {
                            c = (line.get(j) != null) ? '┴' : '┘';
                        } else {
                            if (line.get(j) != null) c = '└';
                        }
                    }
                    out.print(c);

                    // lines and spaces
                    if (line.get(j) == null) {
                        for (int k = 0; k < perpiece - 1; k++) {
                            out.print(" ");
                        }
                    } else {

                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? " " : "─");
                        }
                        out.print(j % 2 == 0 ? "┌" : "┐");
                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? "─" : " ");
                        }
                    }
                }
                out.println();
            }

            // print line of numbers
            for (String f : line) {

                if (f == null) f = "";
                int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                // a number
                for (int k = 0; k < gap1; k++) {
                    out.print(" ");
                }
                out.print(f);
                for (int k = 0; k < gap2; k++) {
                    out.print(" ");
                }
            }
            out.println();

            perpiece /= 2;
        }
    }

}
