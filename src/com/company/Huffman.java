package com.company;

import com.sun.istack.internal.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Huffman {
    private static class Node {
        String code; // Code of encoding value of that symbol
        Node  left, right;
        String symbol;
        int prob;

        Node(String symbol, int prob) {
            this.symbol = symbol;
            this.prob = prob;
            left = right  = null;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            if (this.code == null || this.code.equals("")) return "root";
            //return this.symbol + " : " + this.code + " " + String.format("%.2f",this.prob);
            return this.symbol.replace(othersCode+"","(OTHERS)") + " : " + this.code + " " + this.prob;
        }

    }

    final static int MAX_CHARACTERS = 256;
    final static char othersCode = '\b';
    static String codes[] = new String[MAX_CHARACTERS];
    static int[] freq = new int[MAX_CHARACTERS];
    static Node root , others;

    static void init() {
        root = others = null;
        Arrays.fill(codes,null);
        Arrays.fill(freq,0);
    }

    static void printQueue(PriorityQueue<Node> queue) {
        System.out.println("--------------------------------");
        PriorityQueue<Node> pq = new PriorityQueue<>(queue);
        while (pq.size() > 0) {
            System.out.println(pq.poll());
        }
        System.out.println("--------------------------------");
    }
    public static String Encode(String originalData) throws Exception {
        return Encode(originalData,2e-7);
    }
    public static String Encode(String originalData,double minFrequency) throws Exception {
        init();
        String data = prepare(originalData,minFrequency);
        build(data);
        StringBuilder ret = new StringBuilder();
        String code;
        for (int i = 0; i < data.length(); ++i) {
            code = getCode(data.charAt(i));
            ret.append(code);
            if (code.equals(others.code)) ret.append(String.format("%08d",Integer.parseInt(Integer.toBinaryString(originalData.charAt(i)))));

        }
        return ret.toString();
    }

    public static String Decode(String data) throws Exception {
        StringBuilder ret = new StringBuilder();
        String curr = "";
        int idx;
        for (int i = 0; i < data.length(); ++i) {
            curr += data.charAt(i);
            idx = getChar(curr);
            if (idx != -1) {
                if ((char)idx == othersCode)
                {
                    ret.append((char) Integer.parseInt(data.substring(i+1,i+9), 2));
                    i+=8;
                }
                else ret.append((char)idx);
                curr = "";
            }
        }
        if (!curr.equals("")) {
            throw new Exception("Invalid input.. there is a missing code");
        }
        return ret.toString();
    }
    static String prepare(String data,double minFrequency) throws Exception {
        fillFrequencies(data);
        int minFreq = (int) Math.ceil(minFrequency*data.length());
        for (int i = 0 ; i<freq.length ; ++i)
        {
            if (freq[i] > 0 && freq[i]<minFreq)
            {
                freq[othersCode]+=freq[i];
                freq[i] = 0;
                data = data.replaceAll(((char)i)+"",othersCode+"");
            }
        }
        others = new Node(othersCode+"",freq[othersCode]);
        return data;
    }
    static void fillFrequencies(String data) throws Exception {
        for (int i = 0; i < data.length(); ++i) {
            int c = data.charAt(i);
            if (c >= MAX_CHARACTERS) throw new Exception("Invalid input.. cannot handle this character");
            ++freq[c];
        }
    }
    static void build(String data) throws Exception {

//        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.prob));
        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> {
            if (o1.prob == o2.prob) return 1; // to give the second higher probability
            return Integer.compare(o1.prob, o2.prob);
        });
//        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(o -> o.prob));
        for (int i = 0; i < freq.length; ++i) {
            if (freq[i] > 0) {
                Node temp = new Node((char)i + "", freq[i]); // change here to negative if you want to reverse the priority queue
                if (i == othersCode) others = temp;
                queue.add(temp);
            }
        }
        //printQueue(queue);
        Node left, right;
        if (queue.size() == 1)
        {
            root = queue.poll();
            setCode(root.symbol.charAt(0),"1");
            return;
        }
        while (queue.size() > 1) {
            right = queue.poll();
            left = queue.poll();
            Node temp = new Node(left.symbol + right.symbol, left.prob + right.prob);
            temp.left = left;
            temp.right = right;
            queue.add(temp);
        }
        root = queue.poll();
        assert root != null;
        assignCodes(root, "");

    }

    static void assignCodes(Node curr, String code) throws Exception {
//        if (curr.symbol.length() == 1) codes[curr.symbol.charAt(0)] = code;
        if (curr.symbol != null  && curr.symbol.length() == 1) setCode(curr.symbol.charAt(0),code);
        if (curr.right != null) {
            curr.right.setCode(code + "1");
            assignCodes(curr.right, code + "1");
        }
        if (curr.left != null) {
            curr.left.setCode(code + "0");
            assignCodes(curr.left, code + "0");
        }
    }

    static String getCode(char s) throws Exception {
        if (s >= MAX_CHARACTERS) throw new Exception("Invalid input.. cannot handle this character");
        return (codes[s]!=null?codes[s]:others.code);
    }

    static boolean setCode(char symbol, String code) throws Exception {
        if (codes[symbol] != null) return false;
        codes[symbol] = code;
        return true;
    }

    static int getChar(String code) throws Exception {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < codes.length; ++i) {
            if (codes[i] != null && codes[i].startsWith(code)) indexes.add(i);
        }
        if (indexes.size() == 1) return indexes.get(0);
        if (indexes.size() == 0) throw new Exception("Invalid input.. this code is not exists");
        else return -1; // Multiple codes are found
    }

    public static void EnterDictionary(InputStream input, PrintStream out) throws Exception {
        Scanner in = new Scanner(input);
        int freq;
        String frequency;
        String symbol;
        StringBuilder data = new StringBuilder();
        out.println("Enter the dictionary data (symbol) (frequency) enter symbol = -1 to halt");
        while (true) {
            symbol = in.next();
            int check = 0;
            try {
                check = Integer.parseInt(symbol);
            }catch (Exception ignored){

            }
            if (check == -1) break;
            frequency = in.next();
            freq = Integer.parseInt(frequency);
            if (symbol.length()>1 || freq <= 0)
                throw new Exception("Invalid input.. you have entered wrong data");
            data.append(new String(new char[freq]).replaceAll("\0",symbol));
        }
        Encode(data.toString());
    }

    static void PrintTree(@NotNull PrintStream out) {
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
